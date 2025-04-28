package com.example.logviewer.model;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/28 11:38
 */
public class QueueStatus {

    private String queueName;
    private Integer remainingCapacity;

    public QueueStatus() {
    }

    public QueueStatus(String queueName, Integer remainingCapacity) {
        this.queueName = queueName;
        this.remainingCapacity = remainingCapacity;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Integer getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }
}
