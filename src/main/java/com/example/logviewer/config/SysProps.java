package com.example.logviewer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 系统属性配置
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 15:22
 */
@Configuration
@ConfigurationProperties(prefix = "sys")
public class SysProps {

//    @Value("${log.directory}")
    private List<String> logDirectory;

    // 需要监听的文件名的正则表达式
//    @Value("${log.includes}")
    private List<String> includesPattern;

    // 日志队列大小
    private Integer logQueueSize;
    // 线程数
    private Integer threadSize;

    public List<String> getLogDirectory() {
        return logDirectory;
    }

    public void setLogDirectory(List<String> logDirectory) {
        this.logDirectory = logDirectory;
    }

    public List<String> getIncludesPattern() {
        return includesPattern;
    }

    public void setIncludesPattern(List<String> includesPattern) {
        this.includesPattern = includesPattern;
    }

    public Integer getLogQueueSize() {
        return logQueueSize;
    }

    public void setLogQueueSize(Integer logQueueSize) {
        this.logQueueSize = logQueueSize;
    }

    public Integer getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(Integer threadSize) {
        this.threadSize = threadSize;
    }
}
