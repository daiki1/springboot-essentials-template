package com.project.spring_project.service.impl;

import com.project.spring_project.entity.AuditLog;
import com.project.spring_project.repository.AuditLogRepository;
import com.project.spring_project.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Logs an audit entry for a specific user operation.
     *
     * @param userId   The ID of the user performing the operation.
     * @param operation The type of operation performed.
     * @param details   Additional details about the operation.
     */
    @Override
    public void logAudit(Long userId, String operation, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setOperation(operation);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);

        auditLogRepository.save(auditLog);
    }

    /**
     * Retrieves all audit logs.
     *
     * @param pageable Pagination information.
     * @return A list of audit logs.
     */
    @Override
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}