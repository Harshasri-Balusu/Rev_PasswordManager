package org.example.dao;

import org.example.config.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PasswordHistoryDao {

    private static final Logger logger =
            LoggerFactory.getLogger(PasswordHistoryDao.class);

    private final Connection conn = DBConnection.getInstance();

    public void saveHistory(int passwordId, String oldEncryptedPassword) {

        String sql = """
            INSERT INTO password_history (password_id, old_encrypted_password)
            VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passwordId);
            ps.setString(2, oldEncryptedPassword);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving password history", e);
        }
    }
}
