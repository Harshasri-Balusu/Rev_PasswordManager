package org.example.dao;

import org.example.config.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class AccountStatusDao {

    private static final Logger logger =
            LoggerFactory.getLogger(AccountStatusDao.class);

    private final Connection conn = DBConnection.getInstance();

    public boolean isLocked(int userId) {
        String sql = "SELECT status FROM account_status WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "LOCKED".equals(rs.getString("status"));
            }
        } catch (Exception e) {
            logger.error("Failed to check lock status", e);
        }
        return false;
    }

    public int getFailedAttempts(int userId) {
        String sql = "SELECT failed_attempts FROM account_status WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("failed_attempts");
            }
        } catch (Exception e) {
            logger.error("Failed to fetch failed attempts", e);
        }
        return 0;
    }

    public void updateFailedAttempts(int userId, int attempts) {
        String sql = """
        INSERT INTO account_status (user_id, failed_attempts, status)
        VALUES (?, ?, 'ACTIVE')
        ON DUPLICATE KEY UPDATE failed_attempts = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, attempts);
            ps.setInt(3, attempts);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("Failed to update failed attempts");
        }
    }

    public void lockAccount(int userId) {
        String sql = """
        UPDATE account_status
        SET status = 'LOCKED'
        WHERE user_id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();

            logger.warn("Account LOCKED");
        } catch (Exception e) {
            logger.error("Failed to lock account", e);
        }
    }

    public void resetFailedAttempts(int userId) {
        String sql = """
        UPDATE account_status
        SET failed_attempts = 0, status = 'ACTIVE'
        WHERE user_id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("Failed to reset failed attempts", e);
        }
    }
    public void activateAccount(int userId) {
        resetFailedAttempts(userId);
    }

}
