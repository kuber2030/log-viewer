package com.example.logviewer.model;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/28 10:04
 */
public class TableSummaryAnalysis {

    // 物理大小
    private Long physicalSize;
    // 逻辑数据大小
    private Long logicalData;
    // 空闲空间
    private Long freeSpace;
    // 空闲百分比 当 free_percent > 20% 时建议执行VACUUM 回收空闲页
    private String freeSpacePercent;

    public Long getPhysicalSize() {
        return physicalSize;
    }

    public void setPhysicalSize(Long physicalSize) {
        this.physicalSize = physicalSize;
    }

    public Long getLogicalData() {
        return logicalData;
    }

    public void setLogicalData(Long logicalData) {
        this.logicalData = logicalData;
    }

    public Long getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(Long freeSpace) {
        this.freeSpace = freeSpace;
    }

    public String getFreeSpacePercent() {
        return freeSpacePercent;
    }

    public void setFreeSpacePercent(String freeSpacePercent) {
        this.freeSpacePercent = freeSpacePercent;
    }
}
