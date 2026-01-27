package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.example.util.InputValidator;
import org.example.util.PasswordHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao = new UserDao();
    private final AuditLogService auditLogService = new AuditLogService();
    private final AccountStatusService accountStatusService = new AccountStatusService();



    private String lastErrorMessage;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    // Register
    public boolean register(String username, String fullName,
                            String email, String password) {

        if (InputValidator.isBlank(username) || InputValidator.isBlank(fullName)
                || InputValidator.isBlank(email) || InputValidator.isBlank(password)) {

            lastErrorMessage = "All fields are required.";
            return false;
        }

        if (!InputValidator.isValidEmail(email)) {
            lastErrorMessage = "Invalid email address.";
            return false;
        }

        if (!InputValidator.isValidPassword(password)) {
            lastErrorMessage = "Password must be at least 6 characters.";
            return false;
        }

        if (userDao.isUsernameExists(username)) {
            lastErrorMessage = "Username already exists.";
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMasterPassword(
                PasswordHashUtil.hashPassword(password)
        );

        boolean result = userDao.registerUser(user);

        if (!result) {
            lastErrorMessage = "Registration failed due to system error.";
            return false;
        }
        User savedUser = userDao.getUserByUsername(username);
        auditLogService.log(savedUser.getUserId(), "USER_REGISTERED");

        return true;
    }

    // Login
    public User login(String username, String password) {


        if (InputValidator.isBlank(username) || InputValidator.isBlank(password)) {
            lastErrorMessage = "Username and password are required.";
            return null;
        }
        username = username.trim();
        User user = userDao.getUserByUsername(username);

        if (user == null) {
            lastErrorMessage = "User does not exist.";
            return null;
        }

        if (accountStatusService.isAccountLocked(user.getUserId())) {
            lastErrorMessage = "Account is locked. Please contact support.";
            return null;
        }

        if (!PasswordHashUtil.verifyPassword(
                password, user.getMasterPassword())) {

            accountStatusService.recordFailedAttempt(user.getUserId());

            if (accountStatusService.isAccountLocked(user.getUserId())) {
                lastErrorMessage = "Account locked due to multiple failed login attempts.";
            } else {
                lastErrorMessage = "Invalid username or password.";
            }

            auditLogService.log(user.getUserId(), "LOGIN_FAILED");
            return null;
        }

        accountStatusService.resetAttempts(user.getUserId());
        auditLogService.log(user.getUserId(), "LOGIN_SUCCESS");

        return user;
    }

    // Update Profile
    public boolean updateProfile(int userId, String fullName, String email) {

        User existing = userDao.getUserById(userId);

        if (existing == null) {
            lastErrorMessage = "User not found.";
            return false;
        }

        if (InputValidator.isBlank(fullName)) {
            fullName = existing.getFullName();
        }

        if (InputValidator.isBlank(email)) {
            email = existing.getEmail();
        } else if (!email.contains("@")) {
            lastErrorMessage = "Invalid email address.";
            return false;
        }

        boolean result = userDao.updateProfile(userId, fullName, email);

        if (!result) {
            lastErrorMessage = "Profile update failed.";
            return false;
        }
        return true;
    }

    // Change Password
    public boolean changePassword(int userId,
                                  String oldPassword,
                                  String newPassword) {

        if (InputValidator.isBlank(oldPassword) || InputValidator.isBlank(newPassword)) {
            lastErrorMessage = "Password fields cannot be empty.";
            return false;
        }

        if (!InputValidator.isValidPassword(newPassword)) {
            lastErrorMessage = "New password must be at least 6 characters.";
            logger.debug("Change password failed: short password");
            return false;
        }

        if (oldPassword.equals(newPassword)) {
            lastErrorMessage = "New password cannot be same as old password.";
            return false;
        }

        User user = userDao.getUserById(userId);

        if (user == null) {
            lastErrorMessage = "User not found.";
            logger.debug("Change password failed: user not found");
            return false;
        }

        if (!PasswordHashUtil.verifyPassword(
                oldPassword, user.getMasterPassword())) {

            lastErrorMessage = "Old password is incorrect.";
            logger.debug("Change password failed: wrong old password");
            return false;
        }

        boolean result = userDao.updateMasterPassword(
                userId,
                PasswordHashUtil.hashPassword(newPassword)
        );

        if (!result) {
            lastErrorMessage = "Password update failed.";
            return false;
        }
        auditLogService.log(userId, "MASTER_PASSWORD_CHANGED");
        return true;
    }

    public boolean  forgotPassword(int userId,
                                                  String newPassword) {

        if (InputValidator.isBlank(newPassword)) {
            lastErrorMessage = "Password cannot be empty.";
            return false;
        }

        if (!InputValidator.isValidPassword(newPassword)) {
            lastErrorMessage = "Password must be at least 6 characters.";
            return false;
        }

        boolean updated =
                userDao.updateMasterPassword(
                        userId,
                        PasswordHashUtil.hashPassword(newPassword)
                );

        if (!updated) {
            lastErrorMessage = "Password reset failed.";
            return false;
        }
        accountStatusService.activateAccount(userId);
        auditLogService.log(userId, "PASSWORD_RESET");
        return true;
    }

    public User getUserByUsername(String username) {

        return userDao.getUserByUsername(username);
    }

}
