package org.example.util;

public class InputValidator {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
