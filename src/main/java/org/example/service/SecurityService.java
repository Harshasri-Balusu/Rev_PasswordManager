package org.example.service;

import org.example.dao.SecurityQuestionsDao;
import org.example.dao.UserSecurityAnswerDao;
import org.example.model.SecurityAnswer;
import org.example.model.SecurityQuestion;
import org.example.util.PasswordHashUtil;

import java.util.List;

public class SecurityService {

    private final SecurityQuestionsDao questionDao =
            new SecurityQuestionsDao();

    private final UserSecurityAnswerDao answerDao =
            new UserSecurityAnswerDao();

    private String lastErrorMessage;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public List<SecurityQuestion> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    public boolean saveAnswer(int userId,
                              int questionId,
                              String answer) {

        if (answer == null || answer.trim().isEmpty()) {
            lastErrorMessage = "Security answer cannot be empty.";
            return false;
        }

        SecurityAnswer a = new SecurityAnswer();
        a.setUserId(userId);
        a.setQuestionId(questionId);
        a.setAnswerHash(
                PasswordHashUtil.hashPassword(answer)
        );


        boolean saved = answerDao.save(a);

        if (!saved) {
            lastErrorMessage = "Failed to save security answer.";
            return false;
        }

        return true;
    }

    public SecurityQuestion getUserQuestion(int userId) {

        SecurityAnswer a = answerDao.getByUserId(userId);

        if (a == null) {
            lastErrorMessage = "Security question not set for this user.";
            return null;
        }

        return questionDao.getById(a.getQuestionId());
    }

    public boolean verifyAnswer(int userId,
                                String inputAnswer) {

        if (inputAnswer == null || inputAnswer.trim().isEmpty()) {
            lastErrorMessage = "Security answer is required.";
            return false;
        }

        SecurityAnswer stored =
                answerDao.getByUserId(userId);

        if (stored == null) {
            lastErrorMessage = "Security question not found.";
            return false;
        }

        boolean matched =
                PasswordHashUtil.verifyPassword(
                        inputAnswer,
                        stored.getAnswerHash()
                );

        if (!matched) {
            lastErrorMessage = "Incorrect security answer.";
            return false;
        }

        return true;
    }
}
