package ServiceTest;

import org.example.dao.VerificationCodeDao;
import org.example.model.VerificationCode;
import org.example.service.VerificationCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Spy
    private VerificationCodeService verificationCodeService;

    @Mock
    private VerificationCodeDao verificationCodeDao;

    @BeforeEach
    void setUp() throws Exception {
        inject("dao", verificationCodeDao);
    }

    private void inject(String fieldName, Object mock) throws Exception {
        Field field = VerificationCodeService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(verificationCodeService, mock);
    }

    @Test
    void generateCode_success() {
        when(verificationCodeDao.saveCode(any(VerificationCode.class)))
                .thenReturn(true);

        String otp =
                verificationCodeService.generateCode(1);

        assertNotNull(otp);
        assertFalse(otp.isBlank());
    }

    @Test
    void generateCode_daoFailure() {
        when(verificationCodeDao.saveCode(any(VerificationCode.class)))
                .thenReturn(false);

        String otp =
                verificationCodeService.generateCode(1);

        assertNull(otp);
        assertEquals(
                "Failed to generate verification code.",
                verificationCodeService.getLastErrorMessage()
        );
    }

    @Test
    void verifyCode_success() {
        VerificationCode vc = new VerificationCode();
        vc.setCodeId(10);
        vc.setUserId(1);
        vc.setVerificationCode("123456");

        when(verificationCodeDao.getValidCode(1, "123456"))
                .thenReturn(vc);

        boolean result =
                verificationCodeService.verifyCode(1, "123456");

        assertTrue(result);
        verify(verificationCodeDao).markAsUsed(10);
    }

    @Test
    void verifyCode_blankOtp() {
        boolean result =
                verificationCodeService.verifyCode(1, " ");

        assertFalse(result);
        assertEquals(
                "Verification code is required.",
                verificationCodeService.getLastErrorMessage()
        );
    }

    @Test
    void verifyCode_invalidOrExpired() {
        when(verificationCodeDao.getValidCode(1, "000000"))
                .thenReturn(null);

        boolean result =
                verificationCodeService.verifyCode(1, "000000");

        assertFalse(result);
        assertEquals(
                "Invalid or expired verification code.",
                verificationCodeService.getLastErrorMessage()
        );
    }
}

