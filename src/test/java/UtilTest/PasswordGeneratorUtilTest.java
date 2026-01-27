package UtilTest;

import org.example.util.PasswordGeneratorUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorUtilTest {

    @Test
    void generate_validInput_returnsPassword() {
        String pool = "abcABC123";
        String password = PasswordGeneratorUtil.generate(10, pool);

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generate_emptyPool_returnsNull() {
        String password = PasswordGeneratorUtil.generate(8, "");

        assertNull(password);
    }

    @Test
    void generate_nullPool_returnsNull() {
        String password = PasswordGeneratorUtil.generate(8, null);

        assertNull(password);
    }

    @Test
    void generate_invalidLength_returnsNull() {
        String password = PasswordGeneratorUtil.generate(0, "abc");

        assertNull(password);
    }

    @Test
    void generate_passwordUsesOnlyPoolCharacters() {
        String pool = "abcABC123";
        String password = PasswordGeneratorUtil.generate(20, pool);

        assertNotNull(password);
        for (char c : password.toCharArray()) {
            assertTrue(pool.indexOf(c) >= 0);
        }
    }

}
