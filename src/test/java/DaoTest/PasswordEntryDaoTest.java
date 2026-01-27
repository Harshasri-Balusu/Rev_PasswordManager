package DaoTest;

import org.example.config.DBConnection;
import org.example.dao.PasswordEntryDao;
import org.example.model.PasswordEntry;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordEntryDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(PasswordEntryDaoTest.class);

    private static PasswordEntryDao passwordEntryDao;
    private static int testPasswordId;

    private static final int TEST_USER_ID = 1;

    @BeforeAll
    static void init() {
        passwordEntryDao = new PasswordEntryDao();
        logger.info("PasswordEntryDaoTest started");
    }

    private PasswordEntry createPasswordEntry() {
        PasswordEntry entry = new PasswordEntry();
        entry.setUserId(TEST_USER_ID);
        entry.setCategoryId(null);
        entry.setAccountName("Gmail");
        entry.setAccountUsername("ammu@gmail.com");
        entry.setEncryptedPassword("encrypted_pwd");
        entry.setNotes("personal mail");
        return entry;
    }

    @Test
    @Order(1)
    void addPasswordEntry_success() {
        PasswordEntry entry = createPasswordEntry();

        boolean result = passwordEntryDao.addPasswordEntry(entry);

        assertTrue(result);
        logger.info("addPasswordEntry_success passed");
    }

    @Test
    @Order(2)
    void getAllPasswordsByUser_success() {
        List<PasswordEntry> list =
                passwordEntryDao.getAllPasswordsByUser(TEST_USER_ID);

        assertFalse(list.isEmpty());
        testPasswordId = list.getFirst().getPasswordId();
        logger.info("getAllPasswordsByUser_success passed");
    }

    @Test
    @Order(3)
    void getPasswordById_success() {
        PasswordEntry entry =
                passwordEntryDao.getPasswordById(testPasswordId, TEST_USER_ID);

        assertNotNull(entry);
        assertEquals("Gmail", entry.getAccountName());
        logger.info("getPasswordById_success passed");
    }

    @Test
    @Order(4)
    void updatePasswordEntry_success() {
        PasswordEntry entry = new PasswordEntry();
        entry.setPasswordId(testPasswordId);
        entry.setUserId(TEST_USER_ID);
        entry.setAccountName("Gmail Updated");
        entry.setAccountUsername("ammu_new@gmail.com");
        entry.setEncryptedPassword("new_encrypted_pwd");
        entry.setNotes("updated notes");

        boolean result =
                passwordEntryDao.updatePasswordEntry(entry);

        assertTrue(result);
        logger.info("updatePasswordEntry_success passed");
    }

    @Test
    @Order(5)
    void searchByAccountName_success() {
        List<PasswordEntry> list =
                passwordEntryDao.searchByAccountName(
                        TEST_USER_ID, "Gmail");

        assertFalse(list.isEmpty());
        logger.info("searchByAccountName_success passed");
    }

    // ---------------- NEGATIVE TEST CASES ----------------

    @Test
    @Order(6)
    void getPasswordById_notFound() {
        PasswordEntry entry =
                passwordEntryDao.getPasswordById(99999, TEST_USER_ID);

        assertNull(entry);
        logger.info("getPasswordById_notFound passed");
    }

    @Test
    @Order(7)
    void updatePasswordEntry_invalidUser() {
        PasswordEntry entry = new PasswordEntry();
        entry.setPasswordId(testPasswordId);
        entry.setUserId(99999);
        entry.setAccountName("X");
        entry.setAccountUsername("x");
        entry.setEncryptedPassword("x");
        entry.setNotes("x");

        boolean result =
                passwordEntryDao.updatePasswordEntry(entry);

        assertFalse(result);
        logger.info("updatePasswordEntry_invalidUser passed");
    }

    @Test
    @Order(8)
    void deletePasswordEntry_invalidUser() {
        boolean result =
                passwordEntryDao.deletePasswordEntry(
                        testPasswordId, 99999);

        assertFalse(result);
        logger.info("deletePasswordEntry_invalidUser passed");
    }

    @Test
    @Order(9)
    void deletePasswordEntry_success() {
        boolean result =
                passwordEntryDao.deletePasswordEntry(
                        testPasswordId, TEST_USER_ID);

        assertTrue(result);
        logger.info("deletePasswordEntry_success passed");
    }

    @AfterAll
    static void cleanUp() {
        try {
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps =
                    conn.prepareStatement(
                            "DELETE FROM password_entries WHERE user_id = ?"
                    );
            ps.setInt(1, TEST_USER_ID);
            int rows = ps.executeUpdate();

            logger.info("PasswordEntry cleanup completed, rows deleted: {}", rows);
        } catch (Exception e) {
            logger.error("Error during PasswordEntry cleanup", e);
        }
    }
}
