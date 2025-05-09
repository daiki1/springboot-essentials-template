package com.project.spring_project.service;


import com.project.spring_project.entity.AuditLog;

import java.util.List;

public interface AuditLogService {
    void logAudit(Long userId, String operation, String details);
    List<AuditLog> getAllAuditLogs();
}