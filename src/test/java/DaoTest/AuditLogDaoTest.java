package DaoTest;

import org.example.config.DBConnection;
import org.example.dao.AuditLogDao;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditLogDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(AuditLogDaoTest.class);

    private static AuditLogDao auditLogDao;

    private static final int TEST_USER_ID = 1;
    private static final String TEST_ACTION = "LOGIN_SUCCESS";

    @BeforeAll
    static void init() {
        auditLogDao = new AuditLogDao();
        logger.info("AuditLogDaoTest started");
    }

    @Test
    @Order(1)
    void logAction_success() {

        assertDoesNotThrow(() ->
                auditLogDao.logAction(TEST_USER_ID, TEST_ACTION)
        );

        logger.info("logAction_success passed");
    }

    @Test
    @Order(2)
    void logAction_invalidUser() {

        assertDoesNotThrow(() ->
                auditLogDao.logAction(99999, "INVALID_USER_ACTION")
        );

        logger.info("logAction_invalidUser handled gracefully");
    }

    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM audit_logs WHERE user_id = ?"
                    );
            ps.setInt(1, TEST_USER_ID);
            int rows = ps.executeUpdate();

            logger.info("AuditLog cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during AuditLog cleanup", e);
        }
    }
}

