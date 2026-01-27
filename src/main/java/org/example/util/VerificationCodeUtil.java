package org.example.util;

import java.util.Random;

public class VerificationCodeUtil {

    private static final Random random = new Random();

    private VerificationCodeUtil() {}

    public static String generateOtp() {

        return String.valueOf(100000 + random.nextInt(900000));
    }
}

