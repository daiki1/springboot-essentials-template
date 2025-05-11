package com.project.spring_project.controller;

import com.project.spring_project.entity.AuditLog;
import com.project.spring_project.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogService auditLogService;

    /**
     * Endpoint to retrieve all audit logs
     * Only accessible to users with role AUDITOR
     *
     * @return a list of audit logs
     */
    @Operation(summary = "Get all audit logs",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('AUDITOR')")
    @GetMapping("/audit-logs")
    public Page<AuditLog> getAuditLogs(@PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return auditLogService.getAllAuditLogs(pageable);
    }
}
