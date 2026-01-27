package org.example.service;

import org.example.util.PasswordGeneratorUtil;

public class PasswordGeneratorService {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private String lastErrorMessage;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public String generatePassword(
            int length,
            boolean useUpper,
            boolean useLower,
            boolean useNumbers,
            boolean useSpecial) {

        if (length < 8) {
            lastErrorMessage = "Password length must be at least 8 characters.";
            return null;
        }

        StringBuilder pool = new StringBuilder();

        if (useUpper) {
            pool.append(UPPER);
        }
        if (useLower) {
            pool.append(LOWER);
        }
        if (useNumbers) {
            pool.append(NUMBERS);
        }
        if (useSpecial) {
            pool.append(SPECIAL);
        }

        if (pool.isEmpty()) {
            lastErrorMessage = "Select at least one character type.";
            return null;
        }

        return PasswordGeneratorUtil.generate(length, pool.toString());
    }
}

