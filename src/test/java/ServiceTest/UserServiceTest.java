package ServiceTest;

import org.example.dao.UserDao;
import org.example.model.User;
import org.example.service.AccountStatusService;
import org.example.service.AuditLogService;
import org.example.service.UserService;
import org.example.util.PasswordHashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Spy
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private AccountStatusService accountStatusService;

    @BeforeEach
    void setUp() throws Exception {
        inject("userDao", userDao);
        inject("auditLogService", auditLogService);
        inject("accountStatusService", accountStatusService);
    }

    private void inject(String fieldName, Object mock) throws Exception {
        Field field = UserService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(userService, mock);
    }

    @Test
    void register_success() {
        when(userDao.isUsernameExists("ammu")).thenReturn(false);
        when(userDao.registerUser(any(User.class))).thenReturn(true);

        User saved = new User();
        saved.setUserId(1);
        when(userDao.getUserByUsername("ammu")).thenReturn(saved);

        boolean result = userService.register(
                "ammu", "Ammu", "ammu@gmail.com", "password123"
        );

        assertTrue(result);
        verify(auditLogService).log(1, "USER_REGISTERED");
    }

    @Test
    void register_usernameExists() {
        when(userDao.isUsernameExists("ammu")).thenReturn(true);

        boolean result = userService.register(
                "ammu", "Ammu", "ammu@gmail.com", "password123"
        );

        assertFalse(result);
        assertEquals(
                "Username already exists.",
                userService.getLastErrorMessage()
        );
    }

    @Test
    void login_success() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("ammu");
        user.setMasterPassword(
                PasswordHashUtil.hashPassword("password123")
        );

        when(userDao.getUserByUsername("ammu")).thenReturn(user);
        when(accountStatusService.isAccountLocked(1)).thenReturn(false);

        User result = userService.login("ammu", "password123");

        assertNotNull(result);
        verify(accountStatusService).resetAttempts(1);
        verify(auditLogService).log(1, "LOGIN_SUCCESS");
    }

    @Test
    void login_wrongPassword() {
        User user = new User();
        user.setUserId(1);
        user.setMasterPassword(
                PasswordHashUtil.hashPassword("correct")
        );

        when(userDao.getUserByUsername("ammu")).thenReturn(user);
        when(accountStatusService.isAccountLocked(1)).thenReturn(false);
        when(accountStatusService.isAccountLocked(1)).thenReturn(false, false);

        User result = userService.login("ammu", "wrong");

        assertNull(result);
        verify(accountStatusService).recordFailedAttempt(1);
        verify(auditLogService).log(1, "LOGIN_FAILED");
    }

    @Test
    void updateProfile_success() {
        User existing = new User();
        existing.setFullName("Old");
        existing.setEmail("old@gmail.com");

        when(userDao.getUserById(1)).thenReturn(existing);
        when(userDao.updateProfile(1, "New", "new@gmail.com"))
                .thenReturn(true);

        boolean result =
                userService.updateProfile(1, "New", "new@gmail.com");

        assertTrue(result);
    }

    @Test
    void updateProfile_userNotFound() {
        when(userDao.getUserById(1)).thenReturn(null);

        boolean result =
                userService.updateProfile(1, "New", "new@gmail.com");

        assertFalse(result);
        assertEquals(
                "User not found.",
                userService.getLastErrorMessage()
        );
    }

    @Test
    void changePassword_success() {
        User user = new User();
        user.setUserId(1);
        user.setMasterPassword(
                PasswordHashUtil.hashPassword("old123")
        );

        when(userDao.getUserById(1)).thenReturn(user);
        when(userDao.updateMasterPassword(eq(1), anyString()))
                .thenReturn(true);

        boolean result =
                userService.changePassword(1, "old123", "new1234");

        assertTrue(result);
        verify(auditLogService)
                .log(1, "MASTER_PASSWORD_CHANGED");
    }

    @Test
    void forgotPassword_success() {
        when(userDao.updateMasterPassword(eq(1), anyString()))
                .thenReturn(true);

        boolean result =
                userService.forgotPassword(1, "new1234");

        assertTrue(result);
        verify(accountStatusService).activateAccount(1);
        verify(auditLogService).log(1, "PASSWORD_RESET");
    }
}

