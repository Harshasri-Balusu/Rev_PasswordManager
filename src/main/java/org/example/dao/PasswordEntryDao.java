package org.example.dao;

import org.example.config.DBConnection;
import org.example.model.PasswordEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordEntryDao {

    private static final Logger logger = LoggerFactory.getLogger(PasswordEntryDao.class);

    private final Connection conn;

    public PasswordEntryDao(){
        this.conn = DBConnection.getInstance();
    }

    // Add Password Entry
    public boolean addPasswordEntry(PasswordEntry entry) {

        String sql = """
            INSERT INTO password_entries
            (user_id, category_id, account_name, account_username, encrypted_password, notes)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entry.getUserId());

            if (entry.getCategoryId() != null) {
                ps.setInt(2, entry.getCategoryId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, entry.getAccountName());
            ps.setString(4, entry.getAccountUsername());
            ps.setString(5, entry.getEncryptedPassword());
            ps.setString(6, entry.getNotes());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error adding password entry", e);
            return false;

        }
    }

    // Get all Passwords
    public List<PasswordEntry> getAllPasswordsByUser(int userId) {

        String sql = """
        SELECT p.*, c.category_name
        FROM password_entries p
        LEFT JOIN password_categories c
        ON p.category_id = c.category_id
        WHERE p.user_id = ?
        ORDER BY p.created_at DESC
    """;

        List<PasswordEntry> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PasswordEntry entry = mapRow(rs);
                entry.setCategoryName(rs.getString("category_name"));
                list.add(entry);
            }

        } catch (SQLException e) {
            logger.error("Error fetching password entries", e);
        }

        return list;
    }


    // Get Single Password
    public PasswordEntry getPasswordById(int passwordId, int userId) {

        String sql = """
            SELECT * FROM password_entries
            WHERE password_id = ? AND user_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, passwordId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            logger.error("Error fetching password entry", e);
        }
        return null;
    }

    // Update Password Entry
    public boolean updatePasswordEntry(PasswordEntry entry) {

        String sql = """
            UPDATE password_entries
            SET account_name = ?, account_username = ?,
                encrypted_password = ?, notes = ?
            WHERE password_id = ? AND user_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entry.getAccountName());
            ps.setString(2, entry.getAccountUsername());
            ps.setString(3, entry.getEncryptedPassword());
            ps.setString(4, entry.getNotes());
            ps.setInt(5, entry.getPasswordId());
            ps.setInt(6, entry.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating password entry", e);
            return false;
        }
    }

    // Delete Password Entry
    public boolean deletePasswordEntry(int passwordId, int userId) {

        String sql = """
            DELETE FROM password_entries
            WHERE password_id = ? AND user_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, passwordId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error deleting password entry", e);
            return false;
        }
    }

    // Search by Account name
    public List<PasswordEntry> searchByAccountName(
            int userId, String keyword) {

        String sql = """
            SELECT * FROM password_entries
            WHERE user_id = ? AND account_name LIKE ?
        """;

        List<PasswordEntry> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            logger.error("Error searching password entries", e);
        }

        return list;
    }

    private PasswordEntry mapRow(ResultSet rs)
            throws SQLException {

        PasswordEntry entry = new PasswordEntry();

        entry.setPasswordId(rs.getInt("password_id"));
        entry.setUserId(rs.getInt("user_id"));

        int catId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            entry.setCategoryId(catId);
        }

        entry.setAccountName(rs.getString("account_name"));
        entry.setAccountUsername(rs.getString("account_username"));
        entry.setEncryptedPassword(rs.getString("encrypted_password"));
        entry.setNotes(rs.getString("notes"));
        entry.setCreatedAt(rs.getTimestamp("created_at"));
        entry.setUpdatedAt(rs.getTimestamp("updated_at"));


        try {
            rs.findColumn("category_name");
            entry.setCategoryName(rs.getString("category_name"));
        } catch (SQLException e) {
            logger.debug("category_name column not found, skipping mapping");
        }
        return entry;
    }
}


