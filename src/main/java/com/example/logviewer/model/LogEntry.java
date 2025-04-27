package com.example.logviewer.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class LogEntry {

    private String project;
    private String environment;
    private String logFileName;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
@JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = "Asia/Shanghai"  // 指定时区
)
        private LocalDateTime logTime;
    private String level;
    private String threadId;
    private String logger;
    private String message;


    public LogEntry(String project, String environment, String logFileName,
                    LocalDateTime logTime, String level, String threadId,
                    String logger, String message) {
        this.project = project;
        this.environment = environment;
        this.logFileName = logFileName;
        this.logTime = logTime;
        this.level = level;
        this.threadId = threadId;
        this.logger = logger;
        this.message = message;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}