package com.project.spring_project;

import com.project.spring_project.entity.RefreshToken;
import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import com.project.spring_project.payload.request.RefreshTokenRequest;
import com.project.spring_project.repository.RefreshTokenRepository;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.service.RefreshTokenService;
import com.project.spring_project.util.TokenUtils;
import com.project.spring_project.utils.TestUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RefreshTokenServiceTest {

    private RefreshTokenRepository refreshTokenRepositoryMock;
    private RefreshTokenService refreshTokenServiceMock;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TestUserUtil testUserUtil;

    @Autowired
    public RefreshTokenServiceTest(UserRepository userRepository, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository, TestUserUtil testUserUtil) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.testUserUtil = testUserUtil;
    }

    @BeforeEach
    void setup() {
        refreshTokenRepositoryMock = mock(RefreshTokenRepository.class);
        JwtTokenProvider jwtService = mock(JwtTokenProvider.class);
        UserRepository userRepositoryMock = mock(UserRepository.class);
        refreshTokenServiceMock = new RefreshTokenService(refreshTokenRepositoryMock, jwtService, userRepositoryMock);
    }

    private RefreshToken createAndSaveToken(User user, boolean isUsed, Instant expiry) {
        String rawToken = UUID.randomUUID().toString();
        String hash = TokenUtils.hashedToken(rawToken);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setUsed(isUsed);
        token.setExpiryDate(expiry);
        token.setTokenHash(hash);
        token.setRawToken(rawToken); // Only for testing, not stored in DB

        return refreshTokenRepository.save(token);
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

        when(refreshTokenRepositoryMock.findByTokenHash(hashed)).thenReturn(Optional.of(token));

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(rawToken);

        assertThrows(RuntimeException.class, () -> refreshTokenServiceMock.refreshAccessToken(request));
    }

    @Test
    void refreshAccessToken_usedToken_shouldThrow() {
        testUserUtil.registerUserIfNotExists();
        // Given: A token that's marked as used
        User user = userRepository.findByUsername(testUserUtil.getTestUsername())
                .orElseThrow( () -> new RuntimeException("User not found"));

        RefreshToken usedToken = createAndSaveToken(user, true, Instant.now().plus(15, ChronoUnit.MINUTES));

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(usedToken.getRawToken());

        // When / Then
        assertThrows(RuntimeException.class, () -> refreshTokenService.refreshAccessToken(request));

        testUserUtil.deleteTestUser();
    }

    @Test
    void refreshAccessToken_invalidToken_shouldThrow() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("non-existent-token");

        assertThrows(RuntimeException.class, () -> refreshTokenService.refreshAccessToken(request));
    }

    @Test
    void refreshAccessToken_blankToken_shouldThrow() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("");

        assertThrows(RuntimeException.class, () -> refreshTokenService.refreshAccessToken(request));
    }

    @Test
    void cleanOldRefreshTokens_shouldDeleteExpiredOrUsed() {
        testUserUtil.deleteTestUser();
        testUserUtil.registerUserIfNotExists();

        User user = userRepository.findByUsername(testUserUtil.getTestUsername())
                .orElseThrow( () -> new RuntimeException("User not found"));
        // Given: one expired, one used, one valid
        createAndSaveToken(user, true, Instant.now().plus(15, ChronoUnit.MINUTES)); // used
        createAndSaveToken(user, false, Instant.now().minus(40, ChronoUnit.DAYS)); // expired
        createAndSaveToken(user, false, Instant.now().plus(15, ChronoUnit.MINUTES)); // valid

        // When
        refreshTokenService.cleanOldRefreshTokens();

        // Then
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        assertEquals(1, tokens.size());
        assertFalse(tokens.getFirst().isUsed());
        assertTrue(tokens.getFirst().getExpiryDate().isAfter(Instant.now()));

        testUserUtil.deleteTestUser();
    }

}
