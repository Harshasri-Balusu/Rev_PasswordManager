package org.example.dao;

import org.example.config.DBConnection;
import org.example.model.VerificationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class VerificationCodeDao {

    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeDao.class);

    private final Connection conn = DBConnection.getInstance();

    public boolean saveCode(VerificationCode code) {

        String sql = """
            INSERT INTO verification_codes
            (user_id, verification_code, purpose, expires_at)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code.getUserId());
            ps.setString(2, code.getVerificationCode());
            ps.setString(3, code.getPurpose());
            ps.setTimestamp(4, code.getExpiresAt());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Failed to save verification code", e);
            return false;
        }
    }

    public VerificationCode getValidCode(int userId, String code) {

        String sql = """
            SELECT * FROM verification_codes
            WHERE user_id = ?
              AND verification_code = ?
              AND is_used = FALSE
              AND expires_at > CURRENT_TIMESTAMP
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, code);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                VerificationCode vc = new VerificationCode();
                vc.setCodeId(rs.getInt("code_id"));
                vc.setUserId(rs.getInt("user_id"));
                vc.setVerificationCode(rs.getString("verification_code"));
                vc.setPurpose(rs.getString("purpose"));
                vc.setExpiresAt(rs.getTimestamp("expires_at"));
                vc.setUsed(rs.getBoolean("is_used"));
                return vc;
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch verification code", e);
        }
        return null;
    }

    public void markAsUsed(int codeId) {

        String sql =
                "UPDATE verification_codes SET is_used = TRUE WHERE code_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to mark code as used", e);
        }
    }
}
