package com.project.spring_project;

import com.project.spring_project.entity.RefreshToken;
import com.project.spring_project.entity.User;
import com.project.spring_project.payload.request.RefreshTokenRequest;
import com.project.spring_project.repository.RefreshTokenRepository;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.service.RefreshTokenService;
import com.project.spring_project.util.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RefreshTokenServiceTest {
    private RefreshTokenRepository refreshTokenRepository;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setup() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        JwtTokenProvider jwtService = mock(JwtTokenProvider.class);
        UserRepository userRepository = mock(UserRepository.class);
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, jwtService, userRepository);
    }

    @Test
    void refreshAccessToken_expiredToken_shouldThrow() {
        String rawToken = "test-token";
        String hashed = TokenUtils.hashedToken(rawToken);

        RefreshToken token = new RefreshToken();
        token.setTokenHash(hashed);
        token.setUser(new User());
        token.setExpiryDate(Instant.now().minusSeconds(10));
        token.setUsed(false);

        when(refreshTokenRepository.findByTokenHash(hashed)).thenReturn(Optional.of(token));

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(rawToken);

        assertThrows(RuntimeException.class, () -> refreshTokenService.refreshAccessToken(request));
    }

}
