package org.example.service;

import org.example.dao.AuditLogDao;

public class AuditLogService {

    private final AuditLogDao auditLogDao = new AuditLogDao();

    public void log(int userId, String action) {

        auditLogDao.logAction(userId, action);
    }
}
