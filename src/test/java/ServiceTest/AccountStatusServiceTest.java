package ServiceTest;

import org.example.dao.AccountStatusDao;
import org.example.service.AccountStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountStatusServiceTest {

    @Spy
    private AccountStatusService accountStatusService;

    @Mock
    private AccountStatusDao accountStatusDao;

    @BeforeEach
    void setUp() throws Exception {
        inject("dao", accountStatusDao);
    }

    private void inject(String fieldName, Object mock) throws Exception {
        Field field = AccountStatusService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(accountStatusService, mock);
    }

    @Test
    void isAccountLocked_true() {
        when(accountStatusDao.isLocked(1)).thenReturn(true);

        boolean result =
                accountStatusService.isAccountLocked(1);

        assertTrue(result);
    }

    @Test
    void isAccountLocked_false() {
        when(accountStatusDao.isLocked(1)).thenReturn(false);

        boolean result =
                accountStatusService.isAccountLocked(1);

        assertFalse(result);
    }

    @Test
    void recordFailedAttempt_lessThanMax() {
        when(accountStatusDao.getFailedAttempts(1)).thenReturn(1);

        accountStatusService.recordFailedAttempt(1);

        verify(accountStatusDao)
                .updateFailedAttempts(1, 2);
        verify(accountStatusDao, never())
                .lockAccount(anyInt());
    }

    @Test
    void recordFailedAttempt_reachesMax_shouldLock() {
        when(accountStatusDao.getFailedAttempts(1)).thenReturn(2);

        accountStatusService.recordFailedAttempt(1);

        verify(accountStatusDao)
                .lockAccount(1);
        verify(accountStatusDao)
                .updateFailedAttempts(1, 3);
    }

    @Test
    void resetAttempts_success() {
        accountStatusService.resetAttempts(1);

        verify(accountStatusDao)
                .resetFailedAttempts(1);
    }

    @Test
    void activateAccount_success() {
        accountStatusService.activateAccount(1);

        verify(accountStatusDao)
                .activateAccount(1);
    }
}

