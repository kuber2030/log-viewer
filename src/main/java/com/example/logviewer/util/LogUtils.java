package com.example.logviewer.util;

import com.example.logviewer.model.LogEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LogUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

/*    public static List<LogEntry> filterLogsByTime(List<LogEntry> logs, Date startTime, Date endTime) {
        return logs.stream()
                .filter(log -> log.getLogTime().after(startTime) && log.getTimestamp().before(endTime))
                .collect(Collectors.toList());
    }*/

    public static List<LogEntry> filterLogsByThreadId(List<LogEntry> logs, String threadId) {
        return logs.stream()
                .filter(log -> log.getThreadId().equals(threadId))
                .collect(Collectors.toList());
    }

    public static List<LogEntry> filterLogsByText(List<LogEntry> logs, String searchText) {
        return logs.stream()
                .filter(log -> log.getMessage().contains(searchText))
                .collect(Collectors.toList());
    }

    public static Date parseDate(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public static List<LogEntry> paginateLogs(List<LogEntry> logs, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, logs.size());
        return new ArrayList<>(logs.subList(start, end));
    }
}