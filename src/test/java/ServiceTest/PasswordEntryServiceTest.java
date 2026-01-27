package ServiceTest;

import org.example.dao.PasswordEntryDao;
import org.example.dao.PasswordHistoryDao;
import org.example.model.PasswordEntry;
import org.example.service.AuditLogService;
import org.example.service.PasswordEntryService;
import org.example.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordEntryServiceTest {

    @Spy
    private PasswordEntryService passwordEntryService;

    @Mock
    private PasswordEntryDao passwordEntryDao;

    @Mock
    private PasswordHistoryDao passwordHistoryDao;

    @Mock
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() throws Exception {
        inject("passwordEntryDao", passwordEntryDao);
        inject("passwordHistoryDao", passwordHistoryDao);
        inject("auditLogService", auditLogService);
    }

    private void inject(String fieldName, Object mock) throws Exception {
        Field field = PasswordEntryService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(passwordEntryService, mock);
    }

    @Test
    void addPassword_success() {
        when(passwordEntryDao.addPasswordEntry(any(PasswordEntry.class)))
                .thenReturn(true);

        boolean result =
                passwordEntryService.addPassword(
                        1, null,
                        "Gmail", "ammu@gmail.com",
                        "password123", "notes"
                );

        assertTrue(result);
        verify(auditLogService).log(1, "PASSWORD_ADDED");
    }

    @Test
    void addPassword_blankAccountName() {
        boolean result =
                passwordEntryService.addPassword(
                        1, null,
                        "", "user",
                        "password123", null
                );

        assertFalse(result);
        assertEquals(
                "Account name and password are required.",
                passwordEntryService.getLastErrorMessage()
        );
    }

    @Test
    void viewPassword_success() {
        PasswordEntry entry = new PasswordEntry();
        entry.setPasswordId(10);
        entry.setUserId(1);
        entry.setEncryptedPassword(
                EncryptionUtil.encrypt("secret123")
        );

        when(passwordEntryDao.getPasswordById(10, 1))
                .thenReturn(entry);

        PasswordEntry result =
                passwordEntryService.viewPassword(1, 10);

        assertNotNull(result);
        assertEquals("secret123", result.getEncryptedPassword());
    }

    @Test
    void viewPassword_notFound() {
        when(passwordEntryDao.getPasswordById(10, 1))
                .thenReturn(null);

        PasswordEntry result =
                passwordEntryService.viewPassword(1, 10);

        assertNull(result);
        assertEquals(
                "Password entry not found.",
                passwordEntryService.getLastErrorMessage()
        );
    }

    @Test
    void updatePassword_success_withNewPassword() {
        PasswordEntry old = new PasswordEntry();
        old.setPasswordId(10);
        old.setUserId(1);
        old.setAccountName("Gmail");
        old.setAccountUsername("ammu@gmail.com");
        old.setEncryptedPassword(
                EncryptionUtil.encrypt("old123")
        );

        when(passwordEntryDao.getPasswordById(10, 1))
                .thenReturn(old);

        when(passwordEntryDao.updatePasswordEntry(any(PasswordEntry.class)))
                .thenReturn(true);

        boolean result =
                passwordEntryService.updatePassword(
                        1, 10,
                        "Gmail", "ammu@gmail.com",
                        "new1234", "notes"
                );

        assertTrue(result);
        verify(passwordHistoryDao)
                .saveHistory(10, old.getEncryptedPassword());
        verify(auditLogService).log(1, "PASSWORD_UPDATED");
    }

    @Test
    void updatePassword_accountNameMismatch() {
        PasswordEntry old = new PasswordEntry();
        old.setAccountName("Facebook");
        old.setAccountUsername("user");

        when(passwordEntryDao.getPasswordById(10, 1))
                .thenReturn(old);

        boolean result =
                passwordEntryService.updatePassword(
                        1, 10,
                        "Gmail", "user",
                        "new1234", null
                );

        assertFalse(result);
        assertEquals(
                "Account name does not match existing record.",
                passwordEntryService.getLastErrorMessage()
        );
    }

    @Test
    void deletePassword_success() {
        when(passwordEntryDao.deletePasswordEntry(10, 1))
                .thenReturn(true);

        boolean result =
                passwordEntryService.deletePassword(1, 10);

        assertTrue(result);
        verify(auditLogService).log(1, "PASSWORD_DELETED");
    }

    @Test
    void deletePassword_failure() {
        when(passwordEntryDao.deletePasswordEntry(10, 1))
                .thenReturn(false);

        boolean result =
                passwordEntryService.deletePassword(1, 10);

        assertFalse(result);
        assertEquals(
                "Failed to delete password entry.",
                passwordEntryService.getLastErrorMessage()
        );
    }

    @Test
    void searchPasswords_success() {
        when(passwordEntryDao.searchByAccountName(1, "gmail"))
                .thenReturn(List.of(new PasswordEntry()));

        List<PasswordEntry> result =
                passwordEntryService.searchPasswords(1, "gmail");

        assertFalse(result.isEmpty());
    }

    @Test
    void searchPasswords_blankKeyword() {
        List<PasswordEntry> result =
                passwordEntryService.searchPasswords(1, "");

        assertTrue(result.isEmpty());
        assertEquals(
                "Search keyword cannot be empty.",
                passwordEntryService.getLastErrorMessage()
        );
    }
}

