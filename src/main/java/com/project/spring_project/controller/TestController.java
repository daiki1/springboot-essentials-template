package com.project.spring_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // Public endpoint - no authentication required
    @GetMapping("/all")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint. Everyone can see this.");
    }

    // Only accessible to users with role USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok("This endpoint is for users, admins, and auditors.");
    }

    // Only accessible to users with role AUDITOR
    @PreAuthorize("hasRole('AUDITOR') or hasRole('ADMIN')")
    @GetMapping("/auditor")
    public ResponseEntity<String> auditorEndpoint() {
        return ResponseEntity.ok("This endpoint is only for auditors or admin.");
    }

    // Only accessible to users with role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("This endpoint is only for admins.");
    }
}