package com.example.logviewer.repository;

import com.example.logviewer.model.TableAnalysis;
import com.example.logviewer.model.TableSummaryAnalysis;

import java.util.List;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/28 9:38
 */
public interface TableRepository {

    /**
     * 获取所有的table
     * @return
     */
    List<String> getAllTables();

    /**
     * 删除table
     * @param tableName
     */
    void deleteTables(String tableName);

    /**
     * 汇总统计
     * @return
     */
    TableSummaryAnalysis summaryAnalysis();


    /**
     * 根据表名获取表统计信息
     * @return
     */
    List<TableAnalysis> getTableSummaryAnalysis();

}
