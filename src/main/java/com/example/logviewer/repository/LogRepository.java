package com.example.logviewer.repository;

import com.example.logviewer.model.DateRange;
import com.example.logviewer.model.LogEntry;

import java.util.List;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 10:10
 */
public interface LogRepository {


    /**
     * 批量保存日志
     * @param logEntries
     */
    void save(List<LogEntry> logEntries);

    /**
     * 保存日志
     * @param logEntry
     */
    void save(LogEntry logEntry);

    /**
     * 查询日志
     * @param range
     * @param project
     * @param env
     * @param podName
     * @param threadId
     * @param keyword
     * @return
     */
    List<LogEntry> query(DateRange range, String project, String env, String podName, String threadId, String keyword);

}
