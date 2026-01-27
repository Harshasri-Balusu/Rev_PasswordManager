package UtilTest;

import org.example.util.InputValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Test
    void isBlank_null_shouldReturnTrue() {
        assertTrue(InputValidator.isBlank(null));
    }

    @Test
    void isBlank_empty_shouldReturnTrue() {
        assertTrue(InputValidator.isBlank(""));
        assertTrue(InputValidator.isBlank("   "));
    }

    @Test
    void isBlank_valid_shouldReturnFalse() {
        assertFalse(InputValidator.isBlank("hello"));
    }

    @Test
    void isValidPassword_shortPassword_shouldReturnFalse() {
        assertFalse(InputValidator.isValidPassword("abc"));
    }

    @Test
    void isValidPassword_validPassword_shouldReturnTrue() {
        assertTrue(InputValidator.isValidPassword("abc123"));
    }

    @Test
    void isValidPassword_null_shouldReturnFalse() {
        assertFalse(InputValidator.isValidPassword(null));
    }
}

