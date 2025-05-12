package com.project.spring_project.service.user;

import com.project.spring_project.entity.user.RefreshToken;
import com.project.spring_project.entity.user.User;
import com.project.spring_project.dto.request.RefreshTokenRequest;
import com.project.spring_project.dto.response.AuthResponse;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    AuthResponse refreshAccessToken(RefreshTokenRequest request);
    void cleanOldRefreshTokens();
}
