package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    private static final String URL = "jdbc:mysql://localhost:3306/password_manager";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection connection;

    private DBConnection() {
    }

    public static synchronized Connection getInstance() {

        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                logger.info("Database connection established successfully");
            }
        } catch (SQLException e) {
            logger.error("Failed to establish database connection", e);
            throw new RuntimeException("Database connection failed");
        }

        return connection;
    }

    public static synchronized void closeConnection() {

        if (connection != null) {
            try {
                connection.close();
                connection = null;
                logger.info("Database connection closed successfully");
            } catch (SQLException e) {
                logger.error("Failed to close database connection", e);
            }
        }
    }
}
