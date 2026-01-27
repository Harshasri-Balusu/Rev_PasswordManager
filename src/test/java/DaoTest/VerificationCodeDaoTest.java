package DaoTest;

import org.example.config.DBConnection;
import org.example.dao.VerificationCodeDao;
import org.example.model.VerificationCode;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VerificationCodeDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(VerificationCodeDaoTest.class);

    private static VerificationCodeDao verificationCodeDao;
    private static int testCodeId;

    private static final int TEST_USER_ID = 1;
    private static final String TEST_CODE = "123456";
    private static final String TEST_PURPOSE = "RESET_PASSWORD";

    @BeforeAll
    static void init() {
        verificationCodeDao = new VerificationCodeDao();
        logger.info("VerificationCodeDaoTest started");
    }

    private VerificationCode createValidCode() {
        VerificationCode vc = new VerificationCode();
        vc.setUserId(TEST_USER_ID);
        vc.setVerificationCode(TEST_CODE);
        vc.setPurpose(TEST_PURPOSE);
        vc.setExpiresAt(
                Timestamp.from(Instant.now().plusSeconds(300))
        );
        return vc;
    }

    @Test
    @Order(1)
    void saveCode_success() {
        VerificationCode vc = createValidCode();

        boolean result = verificationCodeDao.saveCode(vc);

        assertTrue(result);
        logger.info("saveCode_success passed");
    }

    @Test
    @Order(2)
    void getValidCode_success() {
        VerificationCode vc =
                verificationCodeDao.getValidCode(TEST_USER_ID, TEST_CODE);

        assertNotNull(vc);
        assertEquals(TEST_USER_ID, vc.getUserId());
        assertEquals(TEST_CODE, vc.getVerificationCode());
        assertFalse(vc.isUsed());

        testCodeId = vc.getCodeId();
        logger.info("getValidCode_success passed");
    }

    @Test
    @Order(3)
    void markAsUsed_success() {
        verificationCodeDao.markAsUsed(testCodeId);

        VerificationCode vc =
                verificationCodeDao.getValidCode(TEST_USER_ID, TEST_CODE);

        assertNull(vc); // used codes should not be valid
        logger.info("markAsUsed_success passed");
    }

    @Test
    @Order(4)
    void getValidCode_invalidCode() {
        VerificationCode vc =
                verificationCodeDao.getValidCode(TEST_USER_ID, "wrong_code");

        assertNull(vc);
        logger.info("getValidCode_invalidCode passed");
    }

    @Test
    @Order(5)
    void getValidCode_invalidUser() {
        VerificationCode vc =
                verificationCodeDao.getValidCode(99999, TEST_CODE);

        assertNull(vc);
        logger.info("getValidCode_invalidUser passed");
    }

    @Test
    @Order(6)
    void saveCode_invalidUser() {
        VerificationCode vc = new VerificationCode();
        vc.setUserId(99999); // invalid FK
        vc.setVerificationCode("000000");
        vc.setPurpose("RESET_PASSWORD");
        vc.setExpiresAt(
                Timestamp.from(Instant.now().plusSeconds(300))
        );

        boolean result = verificationCodeDao.saveCode(vc);

        assertFalse(result);
        logger.info("saveCode_invalidUser passed");
    }

    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM verification_codes WHERE user_id = ?"
                    );
            ps.setInt(1, TEST_USER_ID);
            int rows = ps.executeUpdate();

            logger.info("VerificationCode cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during VerificationCode cleanup", e);
        }
    }
}

