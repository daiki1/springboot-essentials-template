package com.project.spring_project.service;

import com.project.spring_project.entity.RefreshToken;
import com.project.spring_project.entity.User;
import com.project.spring_project.dto.request.RefreshTokenRequest;
import com.project.spring_project.dto.response.AuthResponse;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    AuthResponse refreshAccessToken(RefreshTokenRequest request);
    void cleanOldRefreshTokens();
}
