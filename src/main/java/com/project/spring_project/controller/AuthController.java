package com.project.spring_project.controller;

import com.project.spring_project.dto.request.*;
import com.project.spring_project.dto.response.AuthResponse;
import com.project.spring_project.service.AuthService;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}