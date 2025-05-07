package com.example.logviewer.core;


import com.example.logviewer.config.SysProps;
import com.example.logviewer.model.QueueStatus;
import com.example.logviewer.model.RawLine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 16:50
 */
@Component
public class RoutingBlockQueue {

    // 使用并发map，key为project，value为对应的ArrayBlockQueue
    private static final Map<String, BlockingQueue<RawLine>> projectToQueueMap = new ConcurrentHashMap<>();

    private final SysProps sysProps;

    public RoutingBlockQueue(SysProps sysProps) {
        this.sysProps = sysProps;
    }

    /**
     * 添加原始日志到队列中
     * @param rawLine
     */
    public void put(RawLine rawLine) throws InterruptedException {
        // Add the log entry to the array block queue
        String fileName = rawLine.getFileName();
        // getOrDefault不保证并发安全
        // BlockingQueue orDefault = projectToQueueMap.getOrDefault(project, new ArrayBlockingQueue(sysProps.getLogQueueSize()));
        BlockingQueue<RawLine> blockingQueue = projectToQueueMap.computeIfAbsent(fileName, k -> new ArrayBlockingQueue<>(sysProps.getLogQueueSize()));
        blockingQueue.put(rawLine);
    }

    public RawLine poll(String project) {
        BlockingQueue<RawLine> queue = getQueue(project);
        if (queue != null) {
            return queue.poll();
        }
        return null;
    }

    public List<String> getRoutingKeys() {
        return new ArrayList<>(projectToQueueMap.keySet());
    }

    private BlockingQueue<RawLine> getQueue(String project) {
        return projectToQueueMap.get(project);
    }

    /**
     * 获取队列状态
     * @return
     */
    public List<QueueStatus> queueStatus() {
        List<QueueStatus> result = new ArrayList<>();
        List<String> routingKeys = getRoutingKeys();
        for (String routingKey : routingKeys) {
            BlockingQueue<RawLine> queue = getQueue(routingKey);
            result.add(new QueueStatus(routingKey, queue.remainingCapacity()));
        }
        return result;
    }

    public int getRemainingCapacity(String project) {
        BlockingQueue<RawLine> queue = getQueue(project);
        if (queue != null) {
            return queue.remainingCapacity();
        }
        return -1;
    }

}
