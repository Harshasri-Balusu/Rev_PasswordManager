package org.example.service;

import org.example.dao.AccountStatusDao;

public class AccountStatusService {

    private static final int MAX_ATTEMPTS = 3;
    private final AccountStatusDao dao = new AccountStatusDao();

    public boolean isAccountLocked(int userId) {
        return dao.isLocked(userId);
    }

    public void recordFailedAttempt(int userId) {
        int attempts = dao.getFailedAttempts(userId) + 1;

        if (attempts >= MAX_ATTEMPTS) {
            dao.lockAccount(userId);
        }

        dao.updateFailedAttempts(userId, attempts);
    }

    public void resetAttempts(int userId) {

        dao.resetFailedAttempts(userId);
    }

    public void activateAccount(int userId) {

        dao.activateAccount(userId);
    }
}
