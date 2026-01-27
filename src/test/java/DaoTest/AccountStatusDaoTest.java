package DaoTest;

import org.example.config.DBConnection;
import org.example.dao.AccountStatusDao;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountStatusDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(AccountStatusDaoTest.class);

    private static AccountStatusDao accountStatusDao;

    private static final int TEST_USER_ID = 1;

    @BeforeAll
    static void init() {
        accountStatusDao = new AccountStatusDao();
        logger.info("AccountStatusDaoTest started");
    }

    @Test
    @Order(1)
    void updateFailedAttempts_success() {
        assertDoesNotThrow(() ->
                accountStatusDao.updateFailedAttempts(TEST_USER_ID, 2)
        );

        int attempts = accountStatusDao.getFailedAttempts(TEST_USER_ID);
        assertEquals(2, attempts);

        logger.info("updateFailedAttempts_success passed");
    }

    @Test
    @Order(2)
    void getFailedAttempts_success() {
        int attempts =
                accountStatusDao.getFailedAttempts(TEST_USER_ID);

        assertTrue(attempts >= 0);
        logger.info("getFailedAttempts_success passed");
    }

    @Test
    @Order(3)
    void isLocked_falseAfterReset() {

        accountStatusDao.resetFailedAttempts(TEST_USER_ID);

        boolean locked =
                accountStatusDao.isLocked(TEST_USER_ID);

        assertFalse(locked);
        logger.info("isLocked_falseAfterReset passed");
    }


    @Test
    @Order(4)
    void lockAccount_success() {
        assertDoesNotThrow(() ->
                accountStatusDao.lockAccount(TEST_USER_ID)
        );

        assertTrue(accountStatusDao.isLocked(TEST_USER_ID));
        logger.info("lockAccount_success passed");
    }

    @Test
    @Order(5)
    void resetFailedAttempts_success() {
        assertDoesNotThrow(() ->
                accountStatusDao.resetFailedAttempts(TEST_USER_ID)
        );

        int attempts =
                accountStatusDao.getFailedAttempts(TEST_USER_ID);

        assertEquals(0, attempts);
        assertFalse(accountStatusDao.isLocked(TEST_USER_ID));

        logger.info("resetFailedAttempts_success passed");
    }

    @Test
    @Order(6)
    void activateAccount_success() {
        assertDoesNotThrow(() ->
                accountStatusDao.activateAccount(TEST_USER_ID)
        );

        assertEquals(
                0,
                accountStatusDao.getFailedAttempts(TEST_USER_ID)
        );
        assertFalse(accountStatusDao.isLocked(TEST_USER_ID));

        logger.info("activateAccount_success passed");
    }

    @Test
    @Order(7)
    void getFailedAttempts_invalidUser() {
        int attempts =
                accountStatusDao.getFailedAttempts(99999);

        assertEquals(0, attempts);
        logger.info("getFailedAttempts_invalidUser passed");
    }

    @Test
    @Order(8)
    void isLocked_invalidUser() {
        boolean locked =
                accountStatusDao.isLocked(99999);

        assertFalse(locked);
        logger.info("isLocked_invalidUser passed");
    }

    @Test
    @Order(9)
    void updateFailedAttempts_invalidUser() {
        assertDoesNotThrow(() ->
                accountStatusDao.updateFailedAttempts(99999, 3)
        );

        logger.info("updateFailedAttempts_invalidUser handled gracefully");
    }


    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM account_status WHERE user_id = ?"
                    );
            ps.setInt(1, TEST_USER_ID);
            int rows = ps.executeUpdate();

            logger.info("AccountStatus cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during AccountStatus cleanup", e);
        }
    }
}

