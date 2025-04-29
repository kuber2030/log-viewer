package com.example.logviewer.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.logviewer.model.TableAnalysis;
import com.example.logviewer.model.TableSummaryAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.logviewer.factory.SQLiteConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/28 9:40
 */
@Component
public class SQLiteTableRepository implements TableRepository{

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteTableRepository.class);

    @Override
    public List<String> getAllTables() {
        // 查询SQLite数据库中的所有表名
        try (Connection connection = SQLiteConnectionFactory.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            List<String> tableNames = new ArrayList<>();
            while (resultSet.next()) {
                tableNames.add(resultSet.getString("name"));
            }
            return tableNames;
        } catch (SQLException e) {
            LOGGER.error("Error retrieving table names from SQLite database", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteTables(String tableName) {
        // 删除指定的表
        try (Connection connection = SQLiteConnectionFactory.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
            LOGGER.error("Error deleting table from SQLite database", e);
        }
    }

    @Override
    public TableSummaryAnalysis summaryAnalysis() {
        String sql = "SELECT\n" +
                "    (SELECT page_count * page_size FROM pragma_page_count(), pragma_page_size()) AS physical_size_bytes,\n" + // 物理大小，使用行内PRAGMA 函数, pragma_page_count() 和 pragma_page_size() 是 函数式 PRAGMA，可以像普通函数一样在 SQL 中使用\n" +
                "\n" +
                "    (SELECT SUM(pgsize) FROM dbstat) AS logical_data_bytes,\n" + //  " -- 逻辑数据大小，计算所有表/索引的实际占用空间（不含空闲页）
                "\n" +
                "    (\n" +
                "        (SELECT page_count * page_size FROM pragma_page_count(), pragma_page_size()) -\n" + // 空闲空间
                "        (SELECT SUM(pgsize) FROM dbstat)\n" +
                "    ) AS free_space_bytes,\n" +
                "\n" +
                "    (\n" +
                "        ((SELECT page_count * page_size FROM pragma_page_count(), pragma_page_size()) -\n" +
                "         (SELECT SUM(pgsize) FROM dbstat)) * 100.0 /\n" +
                "        (SELECT page_count * page_size FROM pragma_page_count(), pragma_page_size())\n" +
                "    ) AS free_space_percent;"; // 空闲百分比

        try (Connection connection = SQLiteConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                TableSummaryAnalysis tableSummaryAnalysis = new TableSummaryAnalysis();
                tableSummaryAnalysis.setPhysicalSize(resultSet.getLong("physical_size_bytes") / 1024);
                tableSummaryAnalysis.setLogicalData(resultSet.getLong("logical_data_bytes") / 1024);
                tableSummaryAnalysis.setFreeSpace(resultSet.getLong("free_space_bytes") / 1024);
                BigDecimal free_space_percent = resultSet.getBigDecimal("free_space_percent");
                if (free_space_percent != null) {
                    free_space_percent.setScale(6, RoundingMode.HALF_UP);
                    tableSummaryAnalysis.setFreeSpacePercent(free_space_percent + "%");
                }
                return tableSummaryAnalysis;
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving table names from SQLite database", e);
        }
        return null;
    }

    @Override
    public List<TableAnalysis> getTableSummaryAnalysis() {
        List<TableAnalysis> tableAnalyses = new ArrayList<>();
        String sql = "SELECT\n" +
                "    name AS object_name,\n" +
                "    SUM(pgsize) AS size_bytes,\n" +
                "    SUM(pgsize) / 1024.0 AS size_kb\n" +
                "FROM\n" +
                "    dbstat\n" +
                "GROUP BY\n" +
                "    name\n" +
                "ORDER BY\n" +
                "    size_bytes DESC;";
        try (Connection connection = SQLiteConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             while (resultSet.next()) {
                 TableAnalysis tableAnalysis = new TableAnalysis();
                 tableAnalysis.setTableName(resultSet.getString("object_name"));
                 tableAnalysis.setSizeBytes(resultSet.getLong("size_bytes"));
                 tableAnalysis.setSizeKb(resultSet.getLong("size_kb"));
                 tableAnalyses.add(tableAnalysis);

             }
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return tableAnalyses;
    }

    @Override
    public synchronized void vacuum() {
        try (Connection connection = SQLiteConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("VACUUM")) {

        } catch (Exception e) {
            LOGGER.error("vacuum operation error", e);
        }
    }

}
