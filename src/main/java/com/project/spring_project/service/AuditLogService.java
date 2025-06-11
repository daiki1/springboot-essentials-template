package com.project.spring_project.service;


import com.project.spring_project.dto.AuditLogDto;
import com.project.spring_project.entity.AuditLog;
import com.project.spring_project.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void logAudit(User user, String operation, String details);
    Page<AuditLogDto> getAllAuditLogs(Pageable pageable) ;
}