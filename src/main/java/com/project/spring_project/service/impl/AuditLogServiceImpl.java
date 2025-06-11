package com.project.spring_project.service.impl;

import com.project.spring_project.dto.AuditLogDto;
import com.project.spring_project.dto.request.RequestContext;
import com.project.spring_project.entity.AuditLog;
import com.project.spring_project.entity.user.User;
import com.project.spring_project.mapper.AuditLogMapper;
import com.project.spring_project.repository.AuditLogRepository;
import com.project.spring_project.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    /**
     * Logs an audit entry for a specific user operation.
     *
     * @param user      The user performing the operation.
     * @param operation The type of operation performed.
     * @param details   Additional details about the operation.
     */
    @Override
    public void logAudit(User user, String operation, String details) {
        HttpServletRequest request = RequestContext.getRequest();

        String ipAddress = null;
        String resource = null;

        if (request != null) {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }

            resource = request.getRequestURI();
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setOperation(operation);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);
        auditLog.setResource(resource);
        auditLog.setIpAddress(ipAddress);

        auditLogRepository.save(auditLog);
    }

    /**
     * Retrieves all audit logs.
     *
     * @param pageable Pagination information.
     * @return A list of audit logs.
     */
    @Override
    public Page<AuditLogDto> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(auditLogMapper::toDto);
    }
}