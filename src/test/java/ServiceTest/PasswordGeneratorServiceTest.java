package ServiceTest;

import org.example.service.PasswordGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorServiceTest {

    private PasswordGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new PasswordGeneratorService();
    }

    @Test
    void generatePassword_allOptions_success() {
        String password =
                service.generatePassword(
                        12, true, true, true, true
                );

        assertNotNull(password);
        assertEquals(12, password.length());
    }

    @Test
    void generatePassword_onlyLowercase_success() {
        String password =
                service.generatePassword(
                        10, false, true, false, false
                );

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_upperAndNumbers_success() {
        String password =
                service.generatePassword(
                        8, true, false, true, false
                );

        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    void generatePassword_lengthTooShort() {
        String password =
                service.generatePassword(
                        6, true, true, true, true
                );

        assertNull(password);
        assertEquals(
                "Password length must be at least 8 characters.",
                service.getLastErrorMessage()
        );
    }

    @Test
    void generatePassword_noCharacterTypesSelected() {
        String password =
                service.generatePassword(
                        10, false, false, false, false
                );

        assertNull(password);
        assertEquals(
                "Select at least one character type.",
                service.getLastErrorMessage()
        );
    }
}

