package com.example.logviewer.repository;

import com.example.logviewer.exception.LogEntryWriteException;
import com.example.logviewer.factory.SQLiteConnectionFactory;
import com.example.logviewer.model.DateRange;
import com.example.logviewer.model.LogEntry;
import com.example.logviewer.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 11:05
 */
@Component
public class SQLiteRepository implements LogRepository {

    private static final Logger LOGGER  = LoggerFactory.getLogger(SQLiteRepository.class);
    private static final Set<String> tables = new ConcurrentSkipListSet<>();

    @Override
    public void save(List<LogEntry> logEntries) {
        try (Connection connection = SQLiteConnectionFactory.getConnection()) {
            createTableIfNotExistsJDBC(getTableName(logEntries.get(0)));
            connection.setAutoCommit(false);
            String sql = "insert into " + getTableName(logEntries.get(0)) + "(project, environment, logTime, level, threadId, logger, message) values( ?, ?, ?, ?, ?, ?, ?)";
            System.out.println(sql);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (LogEntry logEntry : logEntries) {
                makePrepartedStatement(logEntry, preparedStatement);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new LogEntryWriteException("save logEntry to sqlite error", e);
        }

    }

    private void createTableIfNotExistsJDBC(String tableName) throws SQLException {
        if (!tableExistsJDBC(tableName)) {
            SQLiteConnectionFactory.createTableIfNotExists(tableName);
        }
    }

    @Override
    public void save(LogEntry logEntry) {
        try (Connection connection = SQLiteConnectionFactory.getConnection()) {
            createTableIfNotExistsJDBC(getTableName(logEntry));
            PreparedStatement preparedStatement = connection.prepareStatement("insert into " + getTableName(logEntry) + "(project, environment, logTime, level, threadId, logger, message) values ( ?, ?, ?, ?, ?, ?, ?)");
            makePrepartedStatement(logEntry, preparedStatement);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new LogEntryWriteException("save logEntry to sqlite error", e);
        }

    }

    private static void makePrepartedStatement(LogEntry logEntry, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, logEntry.getProject());
        preparedStatement.setString(2, logEntry.getEnvironment());
        preparedStatement.setDate(3, DateUtils.convertToSqlDate(logEntry.getLogTime()));
        preparedStatement.setString(4, logEntry.getLevel());
        preparedStatement.setString(5, logEntry.getThreadId());
        preparedStatement.setString(6, logEntry.getLogger());
        preparedStatement.setString(7, logEntry.getMessage());
    }

    @Override
    public List<LogEntry> query(DateRange range, String project, String env, String level, String threadId,
                                String keyword, int page, int size) {
        List<LogEntry> logEntries = new ArrayList<>();
        try (Connection connection = SQLiteConnectionFactory.getConnection()) {
            LocalDateTime startDateTime = DateUtils.convertToLocalDateTime(range.getStartTimestamp());
            LocalDateTime endDateTime = DateUtils.convertToLocalDateTime(range.getEndTimestamp());
            Assert.isTrue(DateUtils.convertToYYYYMM(startDateTime).equals(DateUtils.convertToYYYYMM(endDateTime)), "LOG_VIEWER此版本暂不支持跨月份查询");
            String tableName = project.replace("-", "_") + "_" + DateUtils.convertToYYYYMM(startDateTime);
            List<Object> params = new ArrayList<>();
            String sql = "SELECT project, environment, logTime, level, threadId, logger, message FROM " + tableName + " WHERE 1=1";
            if (startDateTime != null && endDateTime != null) {
                sql += " AND logTime between ? and ?";
                params.add(DateUtils.convertToSqlDate(startDateTime));
                params.add(DateUtils.convertToSqlDate(endDateTime));
            }
            if (StringUtils.hasText(env)) {
                sql += " AND environment = ?";
                params.add(env);
            }
            if (StringUtils.hasText(level)) {
                sql += " AND level = ?";
                params.add(level);
            }
            if (StringUtils.hasText(threadId)) {
                sql += " AND threadId = ?";
                params.add(threadId);
            }
            // kerword 模糊搜索？
            if (StringUtils.hasText(keyword)) {
                sql += " AND message like ?";
                params.add("%" + keyword + "%");
            }

            sql += String.format(" limit %d, %d", page * size, (page+ 1) *size); // page 页号从0开始
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LogEntry logEntry = new LogEntry(resultSet.getString("project"),
                        resultSet.getString("environment"),
                        null,
                        DateUtils.convertToLocalDateTime(resultSet.getTimestamp("logTime")),
                        resultSet.getString("level"),
                        resultSet.getString("threadId"),
                        resultSet.getString("logger"),
                        resultSet.getString("message")
                );
                logEntries.add(logEntry);
            }
        } catch (SQLException e) {
            LOGGER.warn("查询日志失败", e);
        }
        return logEntries;
    }

    protected String getTableName(LogEntry logEntry) {
        String project = logEntry.getProject();
        String suffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return String.format("%s_%s", project.replace("-", "_"), suffix); // sqlite不支持-，所以用_替换
    }

    public static boolean tableExistsJDBC(String tableName) {
        if (tables.contains(tableName)) {
            return true;
        }
        try (ResultSet rs = SQLiteConnectionFactory.getConnection().getMetaData().getTables(null, null, tableName, null)) {
            if (rs.next()) {
                tables.add(tableName);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
