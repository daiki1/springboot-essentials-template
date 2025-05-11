package com.project.spring_project.service;


import com.project.spring_project.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void logAudit(Long userId, String operation, String details);
    Page<AuditLog> getAllAuditLogs(Pageable pageable) ;
}