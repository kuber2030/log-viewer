package com.example.logviewer.model;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/28 10:26
 */
public class TableAnalysis {

    // 表名
    private String tableName;
    // 逻辑数据大小 bytes
    private Long sizeBytes;
    // 逻辑数据大小 kb
    private Long sizeKb;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Long getSizeKb() {
        return sizeKb;
    }

    public void setSizeKb(Long sizeKb) {
        this.sizeKb = sizeKb;
    }
}
