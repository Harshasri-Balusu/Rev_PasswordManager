package DaoTest;

import org.example.config.DBConnection;
import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(UserDaoTest.class);

    private static UserDao userDao;
    private static int testUserId;
    private static final String TEST_USERNAME = "test_user_junit";

    @BeforeAll
    static void init() {
        userDao = new UserDao();
        logger.info("UserDaoTest started");
    }

    private User createUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setFullName("JUnit User");
        user.setEmail("junit@gmail.com");
        user.setMasterPassword("hashed_pwd");
        return user;
    }

    @Test
    @Order(1)
    void registerUser_success() {
        User user = createUser();

        boolean result = userDao.registerUser(user);

        assertTrue(result);
        logger.info("registerUser_success passed");
    }

    @Test
    @Order(2)
    void isUsernameExists_true() {
        boolean exists = userDao.isUsernameExists(TEST_USERNAME);

        assertTrue(exists);
        logger.info("isUsernameExists_true passed");
    }

    @Test
    @Order(3)
    void getUserByUsername_success() {
        User user = userDao.getUserByUsername(TEST_USERNAME);

        assertNotNull(user);
        testUserId = user.getUserId();
        assertEquals("JUnit User", user.getFullName());
        logger.info("getUserByUsername_success passed");
    }

    @Test
    @Order(4)
    void getUserById_success() {
        User user = userDao.getUserById(testUserId);

        assertNotNull(user);
        assertEquals(testUserId, user.getUserId());
        logger.info("getUserById_success passed");
    }

    @Test
    @Order(5)
    void updateProfile_success() {
        boolean updated = userDao.updateProfile(
                testUserId,
                "JUnit Updated",
                "junit_updated@gmail.com"
        );

        assertTrue(updated);
        logger.info("updateProfile_success passed");
    }

    @Test
    @Order(6)
    void updateMasterPassword_success() {
        boolean updated = userDao.updateMasterPassword(
                testUserId,
                "new_hashed_pwd"
        );

        assertTrue(updated);
        logger.info("updateMasterPassword_success passed");
    }

    @Test
    @Order(7)
    void isUsernameExists_false() {
        assertFalse(userDao.isUsernameExists("invalid_user"));
        logger.info("isUsernameExists_false passed");
    }

    @Test
    @Order(8)
    void getUserByUsername_notFound() {
        assertNull(userDao.getUserByUsername("unknown_user"));
        logger.info("getUserByUsername_notFound passed");
    }

    @Test
    @Order(9)
    void getUserById_notFound() {
        assertNull(userDao.getUserById(99999));
        logger.info("getUserById_notFound passed");
    }

    @Test
    @Order(10)
    void updateProfile_invalidUser() {
        boolean result =
                userDao.updateProfile(99999, "X", "x@mail.com");

        assertFalse(result);
        logger.info("updateProfile_invalidUser passed");
    }

    @Test
    @Order(11)
    void updateMasterPassword_invalidUser() {
        boolean result =
                userDao.updateMasterPassword(99999, "pwd");

        assertFalse(result);
        logger.info("updateMasterPassword_invalidUser passed");
    }

    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM users WHERE username = ?"
                    );
            ps.setString(1, TEST_USERNAME);
            int rows = ps.executeUpdate();

            logger.info("Cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during cleanup", e);
        }
    }
}
