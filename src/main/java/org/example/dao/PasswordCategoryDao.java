package org.example.dao;

import org.example.config.DBConnection;
import org.example.model.PasswordCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordCategoryDao {

    private static final Logger logger =
            LoggerFactory.getLogger(PasswordCategoryDao.class);

    private final Connection conn = DBConnection.getInstance();

    public boolean addCategory(int userId, String categoryName) {

        String sql = """
            INSERT INTO password_categories (user_id, category_name)
            VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, categoryName);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Failed to add category", e);
            return false;
        }
    }

    public List<PasswordCategory> getCategoriesByUser(int userId) {

        List<PasswordCategory> list = new ArrayList<>();

        String sql = """
            SELECT * FROM password_categories
            WHERE user_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PasswordCategory c = new PasswordCategory();
                c.setCategoryId(rs.getInt("category_id"));
                c.setUserId(rs.getInt("user_id"));
                c.setCategoryName(rs.getString("category_name"));
                list.add(c);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch categories", e);
        }
        return list;
    }

    public boolean deleteCategory(int categoryId, int userId) {

        String sql = """
            DELETE FROM password_categories
            WHERE category_id = ? AND user_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Failed to delete category", e);
            return false;
        }
    }
}
