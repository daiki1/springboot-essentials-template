package com.project.spring_project.controller;

import com.project.spring_project.util.LocalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    /**
     * Public endpoint - no authentication required
     *
     * @return a message indicating that the public endpoint was accessed
     */
    @Operation(summary = "Public endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Public endpoint accessed successfully")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.public.endpoint"));
    }

    /**
     * Only accessible to users with role USER
     *
     * @return a message indicating that the user endpoint was accessed
     */
    @Operation(summary = "User endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User endpoint accessed successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.user.endpoint"));
    }

    /**
     * Only accessible to users with role AUDITOR
     *
     * @return a message indicating that the auditor endpoint was accessed
     */
    @Operation(summary = "Auditor endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Auditor endpoint accessed successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('AUDITOR') or hasRole('ADMIN')")
    @GetMapping("/auditor")
    public ResponseEntity<String> auditorEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.audit.endpoint"));
    }

    /**
     * Only accessible to users with role ADMIN
     *
     * @return a message indicating that the admin endpoint was accessed
     */
    @Operation(summary = "Admin endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Admin endpoint accessed successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok(localizationService.get("test.admin.endpoint"));
    }
}