package com.project.spring_project.controller;

import com.project.spring_project.util.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final LocalizationService localizationService;

    // Public endpoint - no authentication required
    @GetMapping("/all")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.public.endpoint"));
    }

    // Only accessible to users with role USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.user.endpoint"));
    }

    // Only accessible to users with role AUDITOR
    @PreAuthorize("hasRole('AUDITOR') or hasRole('ADMIN')")
    @GetMapping("/auditor")
    public ResponseEntity<String> auditorEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.audit.endpoint"));
    }

    // Only accessible to users with role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.admin.endpoint"));
    }
}