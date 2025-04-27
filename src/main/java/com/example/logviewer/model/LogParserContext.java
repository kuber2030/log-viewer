package com.example.logviewer.model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 19:59
 */
public class LogParserContext {
    private static final LogParserContext DEFAULT_INSTANCE = new LogParserContext();
    public static LogParserContext defaultContext() {
        return DEFAULT_INSTANCE;
    }

    // 最后一个标准的日志
    private ConcurrentHashMap<String, LogEntry> lastStandardLogEntry= new ConcurrentHashMap(32);
    // 合并半日志行
    private ConcurrentHashMap<String, RawLine> halfLog= new ConcurrentHashMap(32);

    public ConcurrentHashMap<String, LogEntry> getLastStandardLogEntry() {
        return lastStandardLogEntry;
    }

    public ConcurrentHashMap<String, RawLine> getHalfLog() {
        return halfLog;
    }

}
