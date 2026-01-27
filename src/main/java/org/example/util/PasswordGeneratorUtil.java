package org.example.util;

import java.security.SecureRandom;

public class PasswordGeneratorUtil {

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length, String pool) {

        if (pool == null || pool.isEmpty() || length <= 0) {
            return null;
        }

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(pool.length());
            password.append(pool.charAt(index));
        }

        return password.toString();
    }
}
