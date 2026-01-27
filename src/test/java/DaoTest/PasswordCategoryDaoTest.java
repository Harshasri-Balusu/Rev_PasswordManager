package DaoTest;


import org.example.config.DBConnection;
import org.example.dao.PasswordCategoryDao;
import org.example.model.PasswordCategory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordCategoryDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(PasswordCategoryDaoTest.class);

    private static PasswordCategoryDao passwordCategoryDao;
    private static int testCategoryId;

    private static final int TEST_USER_ID = 1;
    private static final String TEST_CATEGORY_NAME = "JUnit Category";

    @BeforeAll
    static void init() {
        passwordCategoryDao = new PasswordCategoryDao();
        logger.info("PasswordCategoryDaoTest started");
    }

    @Test
    @Order(1)
    void addCategory_success() {
        boolean result =
                passwordCategoryDao.addCategory(
                        TEST_USER_ID, TEST_CATEGORY_NAME);

        assertTrue(result);
        logger.info("addCategory_success passed");
    }

    @Test
    @Order(2)
    void getCategoriesByUser_success() {
        List<PasswordCategory> categories =
                passwordCategoryDao.getCategoriesByUser(TEST_USER_ID);

        assertNotNull(categories);
        assertFalse(categories.isEmpty());

        PasswordCategory category = categories.stream()
                .filter(c -> TEST_CATEGORY_NAME.equals(c.getCategoryName()))
                .findFirst()
                .orElse(null);

        assertNotNull(category);
        testCategoryId = category.getCategoryId();

        logger.info("getCategoriesByUser_success passed");
    }

    @Test
    @Order(3)
    void deleteCategory_success() {
        boolean result =
                passwordCategoryDao.deleteCategory(
                        testCategoryId, TEST_USER_ID);

        assertTrue(result);
        logger.info("deleteCategory_success passed");
    }

    @Test
    @Order(4)
    void getCategoriesByUser_invalidUser() {
        List<PasswordCategory> categories =
                passwordCategoryDao.getCategoriesByUser(99999);

        assertNotNull(categories);
        assertTrue(categories.isEmpty());

        logger.info("getCategoriesByUser_invalidUser passed");
    }

    @Test
    @Order(5)
    void deleteCategory_invalidUser() {
        boolean result =
                passwordCategoryDao.deleteCategory(
                        testCategoryId, 99999);

        assertFalse(result);
        logger.info("deleteCategory_invalidUser passed");
    }

    @Test
    @Order(6)
    void addCategory_invalidUser() {
        boolean result =
                passwordCategoryDao.addCategory(
                        99999, "Invalid Category");

        assertFalse(result);
        logger.info("addCategory_invalidUser passed");
    }

    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM password_categories WHERE user_id = ?"
                    );
            ps.setInt(1, TEST_USER_ID);
            int rows = ps.executeUpdate();

            logger.info("PasswordCategory cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during PasswordCategory cleanup", e);
        }
    }
}

