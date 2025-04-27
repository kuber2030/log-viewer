package com.example.logviewer.util;

import java.time.format.DateTimeFormatter;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 14:20
 */
public class DateUtils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER_0 = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_1 = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    // 将时间戳转换为LocalDateTime
    public static java.time.LocalDateTime convertToLocalDateTime(long timestamp) {
        return java.time.Instant.ofEpochMilli(timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    // 将字符串时间转换为LocalDateTime
    public static java.time.LocalDateTime convertToLocalDateTime(String dateString) {
        try {
            return java.time.LocalDateTime.parse(dateString, DATE_TIME_FORMATTER_0);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
        }
    }

    // 将字符串yyyy-MM-dd HH:mm:ss.SSS时间转换为LocalDateTime
    public static java.time.LocalDateTime convertToLocalDateTime(String dateString, DateTimeFormatter dateTimeFormatter) {
        try {
            return java.time.LocalDateTime.parse(dateString, dateTimeFormatter);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
        }
    }

    // 将LocalDateTime转换为“yyyyMM”格式字符串
    public static String convertToYYYYMM(java.time.LocalDateTime localDateTime) {
        return localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
    }

    // 将LocalDateTime转换为“yyyy-MM-dd HH:mm:ss”格式字符串
    public static String convertToYYYYMMDDHHMMSS(java.time.LocalDateTime localDateTime) {
        return localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 将LocalDateTime 转换为 java.sql.Date
    public static java.sql.Date convertToSqlDate(java.time.LocalDateTime localDateTime) {
        return new java.sql.Date(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    // 将java.sql.Date转换为LocalDateTime
    public static java.time.LocalDateTime convertToLocalDateTime(java.sql.Timestamp timestamp) {
        return timestamp.toLocalDateTime();
//        return LocalDateTime.ofInstant(sqlDate.toInstant(), java.time.ZoneId.systemDefault());
    }


}
