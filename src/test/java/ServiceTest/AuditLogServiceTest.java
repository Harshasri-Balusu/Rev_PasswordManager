package ServiceTest;

import org.example.dao.AuditLogDao;
import org.example.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Spy
    private AuditLogService auditLogService;

    @Mock
    private AuditLogDao auditLogDao;

    @BeforeEach
    void setUp() throws Exception {
        inject("auditLogDao", auditLogDao);
    }

    private void inject(String fieldName, Object mock) throws Exception {
        Field field = AuditLogService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(auditLogService, mock);
    }

    @Test
    void log_success() {
        auditLogService.log(1, "LOGIN_SUCCESS");

        verify(auditLogDao)
                .logAction(1, "LOGIN_SUCCESS");
    }

    @Test
    void log_withDifferentAction() {
        auditLogService.log(2, "PASSWORD_CHANGED");

        verify(auditLogDao)
                .logAction(2, "PASSWORD_CHANGED");
    }
}

