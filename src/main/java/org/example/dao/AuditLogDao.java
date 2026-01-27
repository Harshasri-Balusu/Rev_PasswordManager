package org.example.dao;

import org.example.config.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLogDao {

    private static final Logger logger =
            LoggerFactory.getLogger(AuditLogDao.class);

    private final Connection conn;

    public AuditLogDao() {
        this.conn = DBConnection.getInstance();
    }

    public void logAction(int userId, String action) {

        String sql = """
            INSERT INTO audit_logs (user_id, action)
            VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to insert audit log", e);
        }
    }
}
