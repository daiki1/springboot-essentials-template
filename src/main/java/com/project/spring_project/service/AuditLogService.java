package com.project.spring_project.service;

import com.project.spring_project.entity.AuditLog;
import com.project.spring_project.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void logAudit(Long userId, String operation, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setOperation(operation);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}