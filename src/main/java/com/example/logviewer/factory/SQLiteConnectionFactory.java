package com.example.logviewer.factory;

import java.sql.*;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 10:53
 */
public class SQLiteConnectionFactory {
    private static Connection connection;
    private static Connection readConnection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:logs.db");
        }
        return connection;
    }

    public static Connection getReadConnection() throws SQLException {
        if (readConnection == null || readConnection.isClosed()) {
            readConnection = DriverManager.getConnection("jdbc:sqlite:logs.db");
        }
        return readConnection;
    }

    public static void createTableIfNotExists(String tableName) throws SQLException {
        try (Statement stmt = SQLiteConnectionFactory.getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "project VARCHAR(30)," +
                    "environment VARCHAR(30)," +
                    "logFileName VARCHAR(30)," +
                    "logTime DATETIME NOT NULL," +
                    "level VARCHAR(10)," +
                    "threadId VARCHAR(50)," +
                    "logger VARCHAR(100)," +
                    "message TEXT)");
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            System.out.println("Connection established: " + conn);
//            createTableIfNotExists(conn);
            conn.createStatement().executeUpdate("insert into logs (timestamp, level, thread, logger, message, exception) values ('2023-04-24 10:53:00', 'INFO', 'main', 'com.example.logviewer.factory.SQLiteConnectionFactory', 'Hello, SQLite!', 'No exception')");
            ResultSet resultSet = conn.createStatement().executeQuery("select * from logs");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
