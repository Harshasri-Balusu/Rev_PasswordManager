package org.example.dao;

import org.example.config.DBConnection;
import org.example.model.SecurityAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UserSecurityAnswerDao {

    private final Connection conn = DBConnection.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(UserSecurityAnswerDao.class);

    public boolean save(SecurityAnswer answer) {

        String sql = """
            INSERT INTO user_security_answer (user_id, question_id, answer_hash)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
            question_id = VALUES(question_id),
            answer_hash = VALUES(answer_hash)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, answer.getUserId());
            ps.setInt(2, answer.getQuestionId());
            ps.setString(3, answer.getAnswerHash());

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("Failed to save security answer", e);
            return false;
        }

    }

    public SecurityAnswer getByUserId(int userId) {

        String sql = "SELECT * FROM user_security_answer WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                SecurityAnswer a = new SecurityAnswer();
                a.setUserId(rs.getInt("user_id"));
                a.setQuestionId(rs.getInt("question_id"));
                a.setAnswerHash(rs.getString("answer_hash"));
                return a;
            }
        } catch (Exception e) {
            logger.error("Failed to get");
        }
        return null;
    }


}
