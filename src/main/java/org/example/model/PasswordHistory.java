package org.example.model;

import java.sql.Timestamp;

public class PasswordHistory {

    private int historyId;
    private int passwordId;
    private String oldEncryptedPassword;
    private Timestamp changedAt;

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(int passwordId) {
        this.passwordId = passwordId;
    }

    public String getOldEncryptedPassword() {
        return oldEncryptedPassword;
    }

    public void setOldEncryptedPassword(String oldEncryptedPassword) {
        this.oldEncryptedPassword = oldEncryptedPassword;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }
}
