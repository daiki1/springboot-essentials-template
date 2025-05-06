package com.project.spring_project.controller;

import com.project.spring_project.payload.request.*;
import com.project.spring_project.payload.response.AuthResponse;
import com.project.spring_project.service.AuthService;
import com.project.spring_project.service.LocalizationService;
import com.project.spring_project.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final LocalizationService localizationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(localizationService.get("user.registered"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/change_roles")
    public ResponseEntity<String> changeRoles(@RequestBody @Valid ChangeRolesRequest request) {
        authService.changeUserRole(request.getUsername(), request.getRoles());
        return ResponseEntity.ok(localizationService.get("user.role.changed"));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestReset(@RequestBody EmailRequest request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(localizationService.get("user.password.reset.sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(localizationService.get("user.password.reset"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/language")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeLanguage(@RequestBody Map<String, String> request) {
        String language = request.get("language");
        authService.changeLanguage(language);
        return ResponseEntity.ok(localizationService.get("user.language.updated"));
    }
}