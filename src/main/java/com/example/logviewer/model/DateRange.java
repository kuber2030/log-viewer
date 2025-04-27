package com.example.logviewer.model;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 10:14
 */
public class DateRange {

    private Long startTimestamp;
    private Long endTimestamp;

    public DateRange(Long startTimestamp, Long endTimestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
}
