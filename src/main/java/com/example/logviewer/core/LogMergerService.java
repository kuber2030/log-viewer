package com.example.logviewer.core;

import com.example.logviewer.config.SysProps;
import com.example.logviewer.model.LogEntry;
import com.example.logviewer.model.LogParserContext;
import com.example.logviewer.model.RawLine;
import com.example.logviewer.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 17:29
 */
@Component
public class LogMergerService implements LifeCycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogMergerService.class);

    private final LogRepository logRepository;
    private final RoutingBlockQueue routingBlockQueue;
    private final SysProps sysProps;
    private final Set<String> processingFiles = Collections.synchronizedSet(new HashSet<>());

    private final LogParserService logParserService;

    private ExecutorService executors;
    private volatile boolean running = true;


    public LogMergerService(LogRepository logRepository, RoutingBlockQueue routingBlockQueue,
                            SysProps sysProps, LogParserService logParserService) {
        this.logRepository = logRepository;
        this.routingBlockQueue = routingBlockQueue;
        this.sysProps = sysProps;
        this.logParserService = logParserService;
    }

    /**
     * 异步写日志
     * @param rawLine
     */
    public void writeAsync(RawLine rawLine) {
        try {
            routingBlockQueue.put(rawLine);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void start() {
        LOGGER.info("LogMergerService started");
        executors = initExecutor();
        writeLogToRepository();
    }

    private void writeLogToRepository() {
        LogParserContext defaultContext = LogParserContext.defaultContext();
        // SQLite不支持并发写入，写入是库级别的锁，因此只能单线程。
        Thread thread = new Thread(() -> {
            while (running) {
                try {
                    // 为每个 project 创建一个专门的处理线程
                    for (String file : getFileNames()) {
                        processProject(file, defaultContext);
                    }
                    TimeUnit.MICROSECONDS.sleep(2000); // 每轮处理完等待600ms
                } catch (Exception e) {
                    // 不响应中断，需要保证该线程永远运行
                    LOGGER.warn("", e);
                }
            }
        });
        thread.setName("SQLite-thread-write-0");
        thread.start();
        // 只启动一个常驻线程负责调度
        /* executors.execute(() -> {
            while (running) {
                try {
                    // 为每个 project 创建一个专门的处理线程
                    for (String file : getFileNames()) {
                        if (!running) {
                            break;
                        }
                        if (!processingFiles.contains(file)) {
                            processingFiles.add(file);
                            executors.execute(() -> {
                                processProject(file, defaultContext);
                            });
                        }
                        processProject(file, defaultContext);
                    }
                    TimeUnit.SECONDS.sleep(1); // 每轮处理完等待1秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 恢复中断状态
                    break;
                }
            }
        });*/
    }

    private void processProject(String fileName, LogParserContext defaultContext) {
        Long startTimestamp = System.currentTimeMillis();
        int count = 0;
        List<RawLine> rawLines = new ArrayList<>(1024);
        
        try {
            while (running && System.currentTimeMillis() < startTimestamp + 100 && count < 64) {
                RawLine rawLine = routingBlockQueue.poll(fileName);
                if (rawLine != null) {
                    rawLines.add(rawLine);
                    count++;
                }
            }

            int reCount = 0;
            int remainingCapacity = routingBlockQueue.getRemainingCapacity(fileName);
            // 剩余空间不足1/4时，flush队列，防止消息积压导致延迟
            if (remainingCapacity >= 0 && remainingCapacity < 256) {
                while (running && reCount < 768) {
                    RawLine rawLine = routingBlockQueue.poll(fileName);
                    if (rawLine != null) {
                        rawLines.add(rawLine);
                        reCount++;
                    }
                }
                LOGGER.info("{} reCount={}", fileName, reCount);
            }
            
            if (!rawLines.isEmpty()) {
                List<LogEntry> logEntries = asLogEntries(defaultContext, rawLines);
                if (!CollectionUtils.isEmpty(logEntries)) {
                    logRepository.save(logEntries);
                }
            }
            Thread.sleep(sysProps.getProcessFileInterval());
        } catch (Exception e) {
            LOGGER.error("Error processing fileName {}", fileName, e);
        }
    }

    private List<LogEntry> asLogEntries(LogParserContext defaultContext, List<RawLine> rawLines) {
        List<LogEntry> logEntries = new ArrayList<>(rawLines.size());
        for (RawLine rawLine : rawLines) {
            List<LogEntry> parsed = logParserService.parse(defaultContext, rawLine);
            if (!CollectionUtils.isEmpty(parsed)) {
                logEntries.addAll(parsed);
            }
        }
        return logEntries;
    }

    private ExecutorService initExecutor() {
        return Executors.newFixedThreadPool(sysProps.getThreadSize(), new ThreadFactory() {
            private AtomicInteger idx = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(false);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.setName("LogMergerService-Thread-" + idx.getAndIncrement());
                return thread;
            }
        });
    }

    private List<String> getFileNames() {
        return routingBlockQueue.getRoutingKeys();
    }
    @Override
    public void stop() {
        LOGGER.info("LogMergerService stopping...");
        running = false;
        executors.shutdown();
        try {
            if (!executors.awaitTermination(30, TimeUnit.SECONDS)) {
                executors.shutdownNow();
            }
        } catch (InterruptedException e) {
            executors.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("LogMergerService stopped");
    }
}
