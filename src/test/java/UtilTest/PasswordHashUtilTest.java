package UtilTest;

import org.example.util.PasswordHashUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashUtilTest {

    @Test
    void hashPassword_shouldNotReturnPlainText() {
        String password = "secret123";
        String hashed = PasswordHashUtil.hashPassword(password);

        assertNotEquals(password, hashed);
        assertNotNull(hashed);
    }

    @Test
    void verifyPassword_correctPassword_shouldReturnTrue() {
        String password = "secret123";
        String hashed = PasswordHashUtil.hashPassword(password);

        assertTrue(PasswordHashUtil.verifyPassword(password, hashed));
    }

    @Test
    void verifyPassword_wrongPassword_shouldReturnFalse() {
        String hashed = PasswordHashUtil.hashPassword("secret123");

        assertFalse(PasswordHashUtil.verifyPassword("wrong", hashed));
    }

}

