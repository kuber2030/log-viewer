package com.example.logviewer.core;

import com.example.logviewer.config.SysProps;
import com.example.logviewer.model.RawLine;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 15:19
 */
@Component
public class LogTailerService implements LifeCycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private final SysProps sysProps;
    private final LogMergerService logMergerService;

    public LogTailerService(SysProps sysProps, LogMergerService logMergerService) {
        this.sysProps = sysProps;
        this.logMergerService = logMergerService;
    }

    FileFileFilter fileFileFilter = new FileFileFilter() {
        @Override
        public boolean accept(File file) {
            if (file == null || !file.isFile()) {
                return false;
            }
            List<String> patterns = sysProps.getIncludesPattern();
            if (CollectionUtils.isEmpty(patterns)) {
                return false;
            }
            for (String patternStr : patterns) {
                try {
                    Pattern pattern = Pattern.compile(patternStr);
                    Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.matches()) {
                        return true;
                    }
                } catch (Exception e) {
                    LOGGER.warn("Invalid regex pattern: {}", patternStr, e);
                }
            }
            return false;
        }
    };

    public void start() {
        if (CollectionUtils.isEmpty(sysProps.getLogDirectory())) {
            LOGGER.warn("log.directory not config");
            return;
        }
        List<FileAlterationObserver> observers = new ArrayList<>();
        for (String directory : sysProps.getLogDirectory()) {
            System.out.println(directory);
            FileAlterationObserver observer = new FileAlterationObserver(new File(directory), fileFileFilter);
            // 监控目录变化
            observer.addListener(new FileAlterationListenerAdaptor() {
                @Override
                public void onFileCreate(File file) {
                    startTailer(directory.substring(directory.lastIndexOf("/") + 1), file);
                }
            });
            observers.add(observer);
        }

        FileAlterationMonitor monitor = new FileAlterationMonitor(1000, observers);
        executor.submit(() -> {
            try {
                monitor.start();
                // 初始处理现有文件
                sysProps.getLogDirectory().stream()
                        .flatMap(logDir -> {
                            File parent = new File(logDir);
                            File[] children = parent.listFiles();
                            return Arrays.stream(children == null ? new File[0] : children);
                        }).forEach((file) -> {
                            Path parent = Paths.get(file.getParent());
                            String project = parent.getName(parent.getNameCount() - 1).toString();
                            startTailer(project, file);
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        executor.shutdown();
    }

    private void startTailer(String project , File file) {
        Tailer.create(file, new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                if (line.trim().isEmpty()) {
                    return;
                }
                System.out.println("监听到日志：" + line);
                logMergerService.writeAsync(new RawLine(project, file.getName(), line, false));
            }

            @Override
            public void endOfFileReached() {
//                logMergerService.writeAsync(new RawLine(project, file.getName(), "", true));
            }

            @Override
            public void fileRotated() {
                // 确保轮转时处理完旧文件，commons io会读取到文件末尾
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }

            @Override
            public void handle(Exception ex) {
                LOGGER.error("TAILER ERROR", ex);
            }
        }, 1000, true); // 1秒刷新，从文件头开始
    }



}