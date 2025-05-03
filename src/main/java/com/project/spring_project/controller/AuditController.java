package com.project.spring_project.controller;

import com.project.spring_project.entity.AuditLog;
import com.project.spring_project.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogService auditLogService;

    @PreAuthorize("hasRole('AUDITOR')")
    @GetMapping("/audit-logs")
    public List<AuditLog> getAuditLogs() {
        return auditLogService.getAllAuditLogs();
    }
}
