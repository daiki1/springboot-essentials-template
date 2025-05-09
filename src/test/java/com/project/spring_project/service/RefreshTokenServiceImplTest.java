package com.project.spring_project.service;

import com.project.spring_project.entity.RefreshToken;
import com.project.spring_project.entity.User;
import com.project.spring_project.dto.request.RefreshTokenRequest;
import com.project.spring_project.repository.RefreshTokenRepository;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.service.impl.RefreshTokenServiceImpl;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.util.TokenUtils;
import com.project.spring_project.utils.TestUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Profile("dev")
public class RefreshTokenServiceImplTest {

    private RefreshTokenRepository refreshTokenRepositoryMock;
    private RefreshTokenServiceImpl refreshTokenServiceImplMock;
    private final UserRepository userRepository;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TestUserUtil testUserUtil;
    private final LocalizationService localizationService;

    @Autowired
    public RefreshTokenServiceImplTest(UserRepository userRepository, RefreshTokenServiceImpl refreshTokenServiceImpl, RefreshTokenRepository refreshTokenRepository, TestUserUtil testUserUtil, LocalizationService localizationService) {
        this.userRepository = userRepository;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.refreshTokenRepository = refreshTokenRepository;
        this.testUserUtil = testUserUtil;
        this.localizationService = localizationService;
    }

    @BeforeEach
    void setup() {
        refreshTokenRepositoryMock = mock(RefreshTokenRepository.class);
        JwtTokenProvider jwtService = mock(JwtTokenProvider.class);
        UserRepository userRepositoryMock = mock(UserRepository.class);
        refreshTokenServiceImplMock = new RefreshTokenServiceImpl(refreshTokenRepositoryMock, jwtService, userRepositoryMock, localizationService);
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

        assertThrows(RuntimeException.class, () -> refreshTokenServiceImplMock.refreshAccessToken(request));
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
        assertThrows(RuntimeException.class, () -> refreshTokenServiceImpl.refreshAccessToken(request));

        testUserUtil.deleteTestUser();
    }

    @Test
    void refreshAccessToken_invalidToken_shouldThrow() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("non-existent-token");

        assertThrows(RuntimeException.class, () -> refreshTokenServiceImpl.refreshAccessToken(request));
    }

    @Test
    void refreshAccessToken_blankToken_shouldThrow() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("");

        assertThrows(RuntimeException.class, () -> refreshTokenServiceImpl.refreshAccessToken(request));
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
        refreshTokenServiceImpl.cleanOldRefreshTokens();

        // Then
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        assertEquals(1, tokens.size());
        assertFalse(tokens.getFirst().isUsed());
        assertTrue(tokens.getFirst().getExpiryDate().isAfter(Instant.now()));

        testUserUtil.deleteTestUser();
    }

}
