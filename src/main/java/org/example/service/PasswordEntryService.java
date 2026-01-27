package org.example.service;

import org.example.dao.PasswordEntryDao;
import org.example.dao.PasswordHistoryDao;
import org.example.model.PasswordEntry;
import org.example.util.EncryptionUtil;
import org.example.util.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PasswordEntryService {

    private static final Logger logger =
            LoggerFactory.getLogger(PasswordEntryService.class);

    private final PasswordEntryDao passwordEntryDao = new PasswordEntryDao();
    private final PasswordHistoryDao passwordHistoryDao = new PasswordHistoryDao();
    private final AuditLogService auditLogService = new AuditLogService();




    private String lastErrorMessage;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    // Add Password Entry
    public boolean addPassword(int userId,
                               Integer categoryId,
                               String accountName,
                               String accountUsername,
                               String plainPassword,
                               String notes) {

        if (InputValidator.isBlank(accountName) || InputValidator.isBlank(plainPassword)) {
            lastErrorMessage = "Account name and password are required.";
            return false;
        }

        if (!InputValidator.isValidPassword(plainPassword)) {
            lastErrorMessage = "Password must be at least 6 characters.";
            logger.debug("Add password failed: short password");
            return false;
        }

        PasswordEntry entry = new PasswordEntry();
        entry.setUserId(userId);
        entry.setCategoryId(categoryId);
        entry.setAccountName(accountName);
        entry.setAccountUsername(accountUsername);
        entry.setEncryptedPassword(
                EncryptionUtil.encrypt(plainPassword)
        );
        entry.setNotes(notes);

        boolean result = passwordEntryDao.addPasswordEntry(entry);

        if (!result) {
            lastErrorMessage = "Failed to save password entry.";
            return false;
        }
        auditLogService.log(userId, "PASSWORD_ADDED");
        return true;
    }

    // List all Passwords
    public List<PasswordEntry> getAllPasswords(int userId) {

        return passwordEntryDao.getAllPasswordsByUser(userId);
    }

    // View Single password
    public PasswordEntry viewPassword(int userId, int passwordId) {

        PasswordEntry entry =
                passwordEntryDao.getPasswordById(passwordId, userId);

        if (entry == null) {
            lastErrorMessage = "Password entry not found.";
            return null;
        }
        entry.setEncryptedPassword(
                EncryptionUtil.decrypt(entry.getEncryptedPassword())
        );

        return entry;
    }

    // Update Password entry
    public boolean updatePassword(int userId,
                                  int passwordId,
                                  String accountName,
                                  String accountUsername,
                                  String newPassword,
                                  String notes) {

        if (InputValidator.isBlank(accountName)) {
            lastErrorMessage = "Account name cannot be empty.";
            return false;
        }

        // Get existing password entry
        PasswordEntry old =
                passwordEntryDao.getPasswordById(passwordId, userId);

        if (old == null) {
            lastErrorMessage = "Password entry not found.";
            return false;
        }

        if (!old.getAccountName().equals(accountName)) {
            lastErrorMessage = "Account name does not match existing record.";
            return false;
        }

        if (!old.getAccountUsername().equals(accountUsername)) {
            lastErrorMessage = "Account username does not match existing record.";
            return false;
        }

        PasswordEntry entry = new PasswordEntry();
        entry.setPasswordId(passwordId);
        entry.setUserId(userId);
        entry.setAccountName(accountName);
        entry.setAccountUsername(accountUsername);
        entry.setNotes(notes);

        if (!InputValidator.isBlank(newPassword)) {

            if (!InputValidator.isValidPassword(newPassword)) {
                lastErrorMessage = "Password must be at least 6 characters.";
                return false;
            }

            String oldPlain =
                    EncryptionUtil.decrypt(old.getEncryptedPassword());

            if (newPassword.equals(oldPlain)) {
                lastErrorMessage =
                        "New password cannot be same as old password.";
                return false;
            }

            passwordHistoryDao.saveHistory(
                    passwordId,
                    old.getEncryptedPassword()
            );

            entry.setEncryptedPassword(
                    EncryptionUtil.encrypt(newPassword)
            );

        } else {
            entry.setEncryptedPassword(old.getEncryptedPassword());
        }

        boolean result =
                passwordEntryDao.updatePasswordEntry(entry);

        if (!result) {
            lastErrorMessage = "Failed to update password entry.";
            return false;
        }
        auditLogService.log(userId, "PASSWORD_UPDATED");
        return true;
    }


    // Delete Password Entry
    public boolean deletePassword(int userId, int passwordId) {

        boolean result =
                passwordEntryDao.deletePasswordEntry(passwordId, userId);

        if (!result) {
            lastErrorMessage = "Failed to delete password entry.";
            return false;
        }
        auditLogService.log(userId, "PASSWORD_DELETED");
        return true;
    }

    // Search Passwords
    public List<PasswordEntry> searchPasswords(int userId,
                                               String keyword) {

        if (InputValidator.isBlank(keyword)) {
            lastErrorMessage = "Search keyword cannot be empty.";
            return List.of();
        }

        return passwordEntryDao
                .searchByAccountName(userId, keyword);
    }
}
