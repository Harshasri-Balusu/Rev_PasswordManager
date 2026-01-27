package DaoTest;

import org.example.config.DBConnection;
import org.example.dao.UserSecurityAnswerDao;
import org.example.model.SecurityAnswer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserSecurityAnswerDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(UserSecurityAnswerDaoTest.class);

    private static UserSecurityAnswerDao userSecurityAnswerDao;

    private static final int TEST_USER_ID = 1;
    private static final int TEST_QUESTION_ID = 1;

    @BeforeAll
    static void init() {
        userSecurityAnswerDao = new UserSecurityAnswerDao();
        logger.info("UserSecurityAnswerDaoTest started");
    }

    private SecurityAnswer createAnswer() {
        SecurityAnswer answer = new SecurityAnswer();
        answer.setUserId(TEST_USER_ID);
        answer.setQuestionId(TEST_QUESTION_ID);
        answer.setAnswerHash("hashed_answer_123");
        return answer;
    }

    @Test
    @Order(1)
    void save_success() {
        SecurityAnswer answer = createAnswer();

        boolean result = userSecurityAnswerDao.save(answer);

        assertTrue(result);
        logger.info("save_success passed");
    }

    @Test
    @Order(2)
    void getByUserId_success() {
        SecurityAnswer answer =
                userSecurityAnswerDao.getByUserId(TEST_USER_ID);

        assertNotNull(answer);
        assertEquals(TEST_USER_ID, answer.getUserId());
        assertEquals(TEST_QUESTION_ID, answer.getQuestionId());

        logger.info("getByUserId_success passed");
    }

    @Test
    @Order(3)
    void save_updateExisting_success() {
        SecurityAnswer updated = new SecurityAnswer();
        updated.setUserId(TEST_USER_ID);
        updated.setQuestionId(TEST_QUESTION_ID);
        updated.setAnswerHash("updated_hash");

        boolean result = userSecurityAnswerDao.save(updated);

        assertTrue(result);

        SecurityAnswer fetched =
                userSecurityAnswerDao.getByUserId(TEST_USER_ID);

        assertEquals("updated_hash", fetched.getAnswerHash());
        logger.info("save_updateExisting_success passed");
    }

    @Test
    @Order(4)
    void getByUserId_notFound() {
        SecurityAnswer answer =
                userSecurityAnswerDao.getByUserId(99999);

        assertNull(answer);
        logger.info("getByUserId_notFound passed");
    }

    @Test
    @Order(5)
    void save_invalidUser() {
        SecurityAnswer answer = new SecurityAnswer();
        answer.setUserId(99999); // invalid FK
        answer.setQuestionId(TEST_QUESTION_ID);
        answer.setAnswerHash("hash");

        boolean result = userSecurityAnswerDao.save(answer);

        assertFalse(result);
        logger.info("save_invalidUser passed");
    }

    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM user_security_answer WHERE user_id = ?"
                    );
            ps.setInt(1, TEST_USER_ID);
            int rows = ps.executeUpdate();

            logger.info("SecurityAnswer cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during SecurityAnswer cleanup", e);
        }
    }
}

