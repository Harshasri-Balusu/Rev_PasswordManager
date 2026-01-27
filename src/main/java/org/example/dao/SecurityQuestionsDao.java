package org.example.dao;

import org.example.config.DBConnection;
import org.example.model.SecurityQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecurityQuestionsDao {

    private final Connection conn = DBConnection.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(SecurityQuestionsDao.class);

    public List<SecurityQuestion> getAllQuestions() {

        List<SecurityQuestion> list = new ArrayList<>();
        String sql = "SELECT * FROM security_questions";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SecurityQuestion q = new SecurityQuestion();
                q.setQuestionId(rs.getInt("question_id"));
                q.setQuestionText(rs.getString("question_text"));
                list.add(q);
            }
        } catch (Exception e) {
            logger.error("Failed to get questions");
        }
        return list;
    }

    public SecurityQuestion getById(int id) {

        String sql = "SELECT * FROM security_questions WHERE question_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                SecurityQuestion q = new SecurityQuestion();
                q.setQuestionId(rs.getInt("question_id"));
                q.setQuestionText(rs.getString("question_text"));
                return q;
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve data");
        }
        return null;
    }
}
