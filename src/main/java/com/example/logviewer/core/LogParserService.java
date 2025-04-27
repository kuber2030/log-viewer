package com.example.logviewer.core;

import com.example.logviewer.model.LogEntry;
import com.example.logviewer.model.LogParserContext;
import com.example.logviewer.model.RawLine;
import com.example.logviewer.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 18:30
 */
@Service
public class LogParserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogParserService.class);
    private static Pattern pattern = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) \\[([^\\]]+)\\] (\\w+) +([^ ]+) - \\[([^:]+):(\\d+)\\] - (.+)$");

    public List<LogEntry> parse(LogParserContext context, RawLine line) {
        List<LogEntry> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(line.getLine());
        if (matcher.matches()) {
            // 上一个完整的日志
            LogEntry logEntry = context.getLastStandardLogEntry().get(line.getFileName());
            if (logEntry != null) {
                RawLine halfLog = context.getHalfLog().get(line.getFileName());
                if (halfLog != null) {
                    LogEntry exceptionLog = new LogEntry(logEntry.getProject(), logEntry.getEnvironment(),
                            logEntry.getLogFileName(), logEntry.getLogTime(), logEntry.getLevel(),
                            logEntry.getThreadId(), logEntry.getLogger(), halfLog.getLine());
                    // 清空半日志
                    LOGGER.debug("清空半日志...");
                    context.getHalfLog().remove(line.getFileName());
                    result.add(exceptionLog);
                }
            }
            LocalDateTime logTime = DateUtils.convertToLocalDateTime(matcher.group(1), DateUtils.DATE_TIME_FORMATTER_1);
            String threadId = matcher.group(2);
            String level = matcher.group(3);
            String logger = matcher.group(4);
            String file = matcher.group(5);
            int lineNumber = Integer.parseInt(matcher.group(6));
            String message = matcher.group(7);
            LogEntry standard = new LogEntry(line.getProject(), line.getEnv(), line.getFileName(),
                    logTime,level, threadId, logger, message);
            context.getLastStandardLogEntry().put(line.getFileName(), standard);
            result.add(standard);
            return result;
        } else { // 凡是不能解析的视作半日志行
            // 上一个半日志
            RawLine rawLine = context.getHalfLog().get(line.getFileName());
            if (rawLine == null) {
                LOGGER.debug("put半日志...");
                context.getHalfLog().put(line.getFileName(), line);
            } else {
                LOGGER.debug("accumulate半日志...");
                rawLine.setLine(rawLine.getLine() + "\n" + line.getLine());
            }
            return result;
        }
    }

}
