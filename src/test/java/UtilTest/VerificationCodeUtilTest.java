package UtilTest;

import org.example.util.VerificationCodeUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VerificationCodeUtilTest {

    @Test
    void generateOtp_shouldReturn6DigitNumber() {
        String otp = VerificationCodeUtil.generateOtp();

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
    }

    @Test
    void generateOtp_shouldGenerateDifferentValues() {
        String otp1 = VerificationCodeUtil.generateOtp();
        String otp2 = VerificationCodeUtil.generateOtp();

        assertNotEquals(otp1, otp2);
    }
}

