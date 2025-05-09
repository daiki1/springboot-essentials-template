package com.project.spring_project.service;

import com.project.spring_project.dto.request.AuthRequest;
import com.project.spring_project.dto.request.RegisterRequest;
import com.project.spring_project.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    void register(RegisterRequest request);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
    void deleteTestUser(String username);
}