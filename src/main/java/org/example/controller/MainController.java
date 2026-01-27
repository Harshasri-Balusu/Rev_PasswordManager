package org.example.controller;

import org.example.config.DBConnection;
import org.example.model.PasswordCategory;
import org.example.model.User;
import org.example.service.*;
import org.example.model.PasswordEntry;
import org.example.model.SecurityQuestion;

import java.util.List;


import org.example.util.PasswordHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class MainController {

    private static final Logger logger =
            LoggerFactory.getLogger(MainController.class);

    private static final Scanner sc = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final PasswordEntryService passwordService = new PasswordEntryService();
    private static final SecurityService securityService = new SecurityService();
    private static final VerificationCodeService verificationService = new VerificationCodeService();
    private static final PasswordCategoryService categoryService = new PasswordCategoryService();
    private static final PasswordGeneratorService passwordGeneratorService = new PasswordGeneratorService();


    public void start() {

        boolean running = true;
        logger.info("Welcome to RevPassword Manager");

        while (running) {
            logger.info("1. Register");
            logger.info("2. Login");
            logger.info("3. Forgot Password");
            logger.info("0. Exit");
            logger.info("Enter choice:");

            int choice = readIntChoice();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> forgotPassword();
                case 0 -> {
                    running = false;
                    logger.info("Exiting application. Thank You!");
                    DBConnection.closeConnection();
                }
                default -> logger.warn("Please enter a valid option.");
            }
        }
    }

    private static int readIntChoice() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private static boolean isOtpFailed(int userId) {

        logger.info("Generating verification code...");
        String otp = verificationService.generateCode(userId);

        if (otp == null) {
            logger.warn("Failed to generate verification code.");
            return false;
        }

        logger.info("OTP: {}", otp);
        logger.info("Enter verification code:");
        String inputOtp = sc.nextLine();

        if (!verificationService.verifyCode(userId, inputOtp)) {
            logger.warn(verificationService.getLastErrorMessage());
            return true;
        }
        return false;
    }


    // Register
    private static void register() {

        logger.info("Enter username:");
        String username = sc.nextLine();

        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username is required.");
            return;
        }

        logger.info("Enter full name:");
        String fullName = sc.nextLine();

        if (fullName == null || fullName.trim().isEmpty()) {
            logger.warn("Full name is required.");
            return;
        }

        logger.info("Enter email:");
        String email = sc.nextLine();

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Email is required.");
            return;
        }

        logger.info("Enter master password:");
        String password = sc.nextLine();

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Master password is required.");
            return;
        }


        boolean success =
                userService.register(username, fullName, email, password);

        if (success) {
            logger.info("Registration successful.");

            User user = userService.getUserByUsername(username);

            List<SecurityQuestion> questions = securityService.getAllQuestions();

            logger.info("Choose Security Question:");
            for (SecurityQuestion q : questions) {
                logger.info("{}. {}", q.getQuestionId(), q.getQuestionText());
            }

            int qid = Integer.parseInt(sc.nextLine());

            logger.info("Enter Security Answer:");
            String ans = sc.nextLine();

            boolean saved = securityService.saveAnswer(user.getUserId(), qid, ans);


            if (saved) {
                logger.info("Security question saved successfully.");
            } else {
                logger.warn(securityService.getLastErrorMessage());
            }


        } else {
            logger.warn(userService.getLastErrorMessage());
        }
    }

    // Login
    private static void login() {

        logger.info("Enter username:");
        String username = sc.nextLine();

        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username is required.");
            return;
        }

        logger.info("Enter master password:");
        String password = sc.nextLine();

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Master password is required.");
            return;
        }

        User user = userService.login(username, password);

        if (user != null) {
            logger.info("Login successful. Welcome, {}", user.getFullName());
            loggedInMenu(user);
        } else {
            logger.warn(userService.getLastErrorMessage());
        }

    }

    // Forgot Password
    private static void forgotPassword() {

        logger.info("Enter username:");
        String username = sc.nextLine();

        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username is required.");
            return;
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            logger.warn("User not found.");
            return;
        }

        SecurityQuestion question =
                securityService.getUserQuestion(user.getUserId());

        if (question == null) {
            logger.warn(securityService.getLastErrorMessage());
            return;
        }

        logger.info("Security Question:");
        logger.info(question.getQuestionText());

        logger.info("Enter your answer:");
        String answer = sc.nextLine();

        boolean verified =
                securityService.verifyAnswer(user.getUserId(), answer);

        if (!verified) {
            logger.warn(securityService.getLastErrorMessage());
            return;
        }

        if (isOtpFailed(user.getUserId())) {
            return;
        }

        logger.info("Enter new master password:");
        String newPassword = sc.nextLine();

        if (newPassword == null || newPassword.trim().isEmpty()) {
            logger.warn("New password is required.");
            return;
        }

        boolean reset =
                userService.forgotPassword(
                        user.getUserId(), newPassword);

        if (reset) {
            logger.info("Password reset successful. Please login.");
        } else {
            logger.warn(userService.getLastErrorMessage());
        }
    }

    // After Login Menu
    private static void loggedInMenu(User user) {

        boolean loggedIn = true;

        while (loggedIn) {
            logger.info("1. Update Profile");
            logger.info("2. Change Master Password");
            logger.info("3. Add Password");
            logger.info("4. List Passwords");
            logger.info("5. View Password");
            logger.info("6. Update Password");
            logger.info("7. Delete Password");
            logger.info("8. Search Password");
            logger.info("9. Add Category");
            logger.info("10. List Categories");
            logger.info("11. Delete Category");
            logger.info("12. Generate Strong Password");

            logger.info("13. Logout");
            logger.info("Enter choice:");

            int choice = readIntChoice();

            switch (choice) {
                case 1 -> updateProfile(user);
                case 2 -> changePassword(user);
                case 3 -> addPassword(user);
                case 4 -> listPasswords(user);
                case 5 -> viewPassword(user);
                case 6 -> updatePasswordEntry(user);
                case 7 -> deletePasswordEntry(user);
                case 8 -> searchPasswords(user);
                case 9 -> addCategory(user);
                case 10 -> listCategories(user);
                case 11 -> deleteCategory(user);
                case 12 -> generatePassword();

                case 13 -> {
                    loggedIn = false;
                    logger.info("Logged out successfully.");
                }
                default -> logger.warn("Please enter a valid option.");
            }
        }

    }

    // Update Profile
    private static void updateProfile(User user) {

        logger.info("Enter new full name:");
        String fullName = sc.nextLine().trim();

        logger.info("Enter new email:");
        String email = sc.nextLine().trim();

        boolean updated =
                userService.updateProfile(
                        user.getUserId(), fullName, email);

        if (updated) {
            user.setFullName(fullName);
            user.setEmail(email);
            logger.info("Profile updated successfully.");
        } else {
            logger.warn(userService.getLastErrorMessage());
        }
    }

    // Change Master Password
    private static void changePassword(User user) {

        logger.info("Enter old password:");
        String oldPassword = sc.nextLine().trim();
        if (oldPassword.isEmpty()) {
            logger.warn("Old password is required.");
            return;
        }

        if (isOtpFailed(user.getUserId())) {
            return;
        }

        logger.info("Enter new password:");
        String newPassword = sc.nextLine().trim();

        if (newPassword.isEmpty()) {
            logger.warn("New password is required.");
            return;
        }

        boolean changed =
                userService.changePassword(
                        user.getUserId(), oldPassword, newPassword);

        if (changed) {
            logger.info("Password changed successfully.");
        } else {
            logger.warn(userService.getLastErrorMessage());
        }
    }

    // Add Account Password
    private static void addPassword(User user) {

        List<PasswordCategory> categories =
                categoryService.getCategories(user.getUserId());

        Integer categoryId = null;

        if (!categories.isEmpty()) {
            logger.info("Select Category (0 = No Category):");
            for (PasswordCategory c : categories) {
                logger.info("{} : {}", c.getCategoryId(), c.getCategoryName());
            }

            int choice = readIntChoice();
            if (choice > 0) {
                categoryId = choice;
            }
        }

        logger.info("Enter account name:");
        String accountName = sc.nextLine().trim();

        if (accountName.isEmpty()) {
            logger.warn("Account name is required.");
            return;
        }

        logger.info("Enter account username:");
        String accountUsername = sc.nextLine().trim();
        if(accountUsername.isEmpty()){
            logger.warn("Account username is required.");
            return;
        }

        logger.info("Enter password:");
        String password = sc.nextLine().trim();
        if (password.isEmpty()) {
            logger.warn("Password is required.");
            return;
        }

        logger.info("Enter notes (optional):");
        String notes = sc.nextLine();

        boolean success = passwordService.addPassword(
                user.getUserId(),
                categoryId,
                accountName,
                accountUsername,
                password,
                notes
        );

        if (success) {
            logger.info("Password saved successfully.");
        } else {
            logger.warn(passwordService.getLastErrorMessage());
        }
    }

    // List Account Passwords
    private static void listPasswords(User user) {

        List<PasswordEntry> list =
                passwordService.getAllPasswords(user.getUserId());

        if (list.isEmpty()) {
            logger.info("No password entries found.");
            return;
        }

        logger.info("Saved Passwords:");
        for (PasswordEntry p : list) {
            String category =
                    (p.getCategoryName() == null)
                            ? "No Category"
                            : p.getCategoryName();

            logger.info("ID: {} | Account: {} | Username: {} | Category: {}",
                    p.getPasswordId(),
                    p.getAccountName(),
                    p.getAccountUsername(),
                    category);

        }
    }

    // View accounts passwords
    private static void viewPassword(User user) {

        logger.info("Enter password ID:");
        int passwordId = readIntChoice();
        if (passwordId <= 0) {
            logger.warn("Invalid password ID.");
            return;
        }

        logger.info("Re-enter master password:");
        String masterPassword = sc.nextLine();
        if (masterPassword.isEmpty()) {
            logger.warn("Master password is required.");
            return;
        }

        if (!PasswordHashUtil.verifyPassword(
                masterPassword, user.getMasterPassword())) {

            logger.warn("Master password incorrect. Access denied.");
            return;
        }

        PasswordEntry entry =
                passwordService.viewPassword(user.getUserId(), passwordId);

        if (entry != null) {
            logger.info("Account: {}", entry.getAccountName());
            logger.info("Username: {}", entry.getAccountUsername());
            logger.info("Password: {} ", entry.getEncryptedPassword());
            logger.info("Notes: {}", entry.getNotes());
        } else {
            logger.warn(passwordService.getLastErrorMessage());
        }
    }

    // Update account password
    private static void updatePasswordEntry(User user) {

        logger.info("Enter password ID:");
        int passwordId = readIntChoice();
        if (passwordId <= 0) {
            logger.warn("Invalid password ID.");
            return;
        }

        logger.info("Enter account name:");
        String accountName = sc.nextLine();
        if (accountName.isEmpty()) {
            logger.warn("Account name cannot be empty.");
            return;
        }

        logger.info("Enter account username:");
        String accountUsername = sc.nextLine();
        if(accountUsername.isEmpty()){
            logger.warn("Account username is required.");
            return;
        }

        logger.info("Generating verification code...");
        String otp = verificationService.generateCode(user.getUserId());
        logger.info("OTP {}: ", otp);

        logger.info("Enter verification code:");
        String inputOtp = sc.nextLine();

        if (!verificationService.verifyCode(user.getUserId(), inputOtp)) {
            logger.info(verificationService.getLastErrorMessage());
            return;

        }

        logger.info("Enter new password: ");
        String newPassword = sc.nextLine();

        logger.info("Enter new notes:");
        String notes = sc.nextLine();

        boolean success = passwordService.updatePassword(
                user.getUserId(),
                passwordId,
                accountName,
                accountUsername,
                newPassword,
                notes
        );

        if (success) {
            logger.info("Password updated successfully.");
        } else {
            logger.warn(passwordService.getLastErrorMessage());
        }
    }

    // Delete account Password
    private static void deletePasswordEntry(User user) {

        logger.info("Enter password ID to delete:");
        int passwordId = readIntChoice();
        if (passwordId <= 0) {
            logger.warn("Invalid password ID.");
            return;
        }

        if (isOtpFailed(user.getUserId())) {
            return;
        }

        boolean success =
                passwordService.deletePassword(user.getUserId(), passwordId);

        if (success) {
            logger.info("Password deleted successfully.");
        } else {
            logger.warn(passwordService.getLastErrorMessage());
        }
    }

    // Search accounts
    private static void searchPasswords(User user) {

        logger.info("Enter account name keyword:");
        String keyword = sc.nextLine();
        if (keyword.isEmpty()) {
            logger.warn("Search keyword is required.");
            return;
        }

        List<PasswordEntry> list =
                passwordService.searchPasswords(user.getUserId(), keyword);

        if (list.isEmpty()) {
            logger.info("No matching passwords found.");
            return;
        }

        for (PasswordEntry p : list) {
            logger.info("ID: {} | Account: {} | Username: {}",
                    p.getPasswordId(),
                    p.getAccountName(),
                    p.getAccountUsername());
        }
    }

    // Add category
    private static void addCategory(User user) {

        logger.info("Enter category name:");
        String name = sc.nextLine();

        boolean added =
                categoryService.addCategory(user.getUserId(), name);

        if (added) {
            logger.info("Category added successfully.");
        } else {
            logger.warn(categoryService.getLastErrorMessage());
        }
    }

    // List categories
    private static void listCategories(User user) {

        List<PasswordCategory> list =
                categoryService.getCategories(user.getUserId());

        if (list.isEmpty()) {
            logger.info("No categories found.");
            return;
        }

        logger.info("Your Categories:");
        for (PasswordCategory c : list) {
            logger.info("ID: {} | Name: {}",
                    c.getCategoryId(),
                    c.getCategoryName());
        }
    }

    // Delete categories
    private static void deleteCategory(User user) {

        logger.info("Enter category ID to delete:");
        int id = readIntChoice();

        boolean deleted =
                categoryService.deleteCategory(id, user.getUserId());

        if (deleted) {
            logger.info("Category deleted successfully.");
        } else {
            logger.warn(categoryService.getLastErrorMessage());
        }
    }

    // Generate strong password
    private static void generatePassword() {

        logger.info("Enter password length (min 8):");
        int length = readIntChoice();

        if (length < 8) {
            logger.warn("Password length must be at least 8.");
            return;
        }

        logger.info("Include Uppercase letters? (y/n)");
        boolean upper = sc.nextLine().equalsIgnoreCase("y");

        logger.info("Include Lowercase letters? (y/n)");
        boolean lower = sc.nextLine().equalsIgnoreCase("y");

        logger.info("Include Numbers? (y/n)");
        boolean numbers = sc.nextLine().equalsIgnoreCase("y");

        logger.info("Include Special characters? (y/n)");
        boolean special = sc.nextLine().equalsIgnoreCase("y");

        String password = passwordGeneratorService.generatePassword(
                        length, upper, lower, numbers, special
                );

        if (password == null) {
            logger.warn(passwordGeneratorService.getLastErrorMessage());
            return;
        }

        logger.info("Generated Strong Password: {}", password);
    }

}






