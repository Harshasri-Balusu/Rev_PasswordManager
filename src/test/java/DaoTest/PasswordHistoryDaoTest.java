package DaoTest;

import org.example.config.DBConnection;
import org.example.dao.PasswordHistoryDao;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordHistoryDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(PasswordHistoryDaoTest.class);

    private static PasswordHistoryDao passwordHistoryDao;

    private static final int TEST_PASSWORD_ID = 1;
    private static final String OLD_PASSWORD = "old_encrypted_pwd";

    @BeforeAll
    static void init() {
        passwordHistoryDao = new PasswordHistoryDao();
        logger.info("PasswordHistoryDaoTest started");
    }

    @Test
    @Order(1)
    void saveHistory_success() {
        assertDoesNotThrow(() ->
                passwordHistoryDao.saveHistory(
                        TEST_PASSWORD_ID, OLD_PASSWORD)
        );

        logger.info("saveHistory_success passed");
    }

    @Test
    @Order(2)
    void saveHistory_invalidPasswordId() {
        assertDoesNotThrow(() ->
                passwordHistoryDao.saveHistory(
                        99999, "pwd")
        );

        logger.info("saveHistory_invalidPasswordId handled gracefully");
    }

    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM password_history WHERE password_id = ?"
                    );
            ps.setInt(1, TEST_PASSWORD_ID);
            int rows = ps.executeUpdate();

            logger.info("PasswordHistory cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during PasswordHistory cleanup", e);
        }
    }
}

