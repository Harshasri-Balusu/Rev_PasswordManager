package UtilTest;

import org.example.util.EncryptionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilTest {

    @Test
    void encryptAndDecrypt_shouldReturnOriginalText() {
        String original = "mypassword123";

        String encrypted = EncryptionUtil.encrypt(original);
        String decrypted = EncryptionUtil.decrypt(encrypted);

        assertEquals(original, decrypted);
    }

    @Test
    void encrypt_shouldNotReturnPlainText() {
        String original = "mypassword123";

        String encrypted = EncryptionUtil.encrypt(original);

        assertNotEquals(original, encrypted);
    }

    @Test
    void testDecryptInvalidData() {
        assertThrows(RuntimeException.class, () -> {
            EncryptionUtil.decrypt("invalidEncryptedText");
        });
    }
}

