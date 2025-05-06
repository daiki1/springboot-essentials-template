package com.project.spring_project.service;

import com.project.spring_project.entity.RefreshToken;
import com.project.spring_project.entity.User;
import com.project.spring_project.payload.request.RefreshTokenRequest;
import com.project.spring_project.payload.response.AuthResponse;
import com.project.spring_project.repository.RefreshTokenRepository;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.project.spring_project.util.TokenUtils.hashedToken;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtService;
    private final UserRepository userRepository;
    private final LocalizationService localizationService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtService, UserRepository userRepository, LocalizationService localizationService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.localizationService = localizationService;
    }


    public RefreshToken createRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashedToken(rawToken); // use the same PasswordEncoder as for passwords

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hashedToken);
        token.setExpiryDate(Instant.now().plus(Duration.ofDays(7))); // or a config value
        refreshTokenRepository.save(token);

        // Set the raw token (NOT saved) so it can be returned to the client
        token.setRawToken(rawToken);
        return token;
    }

    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        String hashedToken = hashedToken(request.getRefreshToken());
        RefreshToken token = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new RuntimeException(localizationService.get("token.refresh.invalid")));

        if (token.isUsed() || token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException(localizationService.get("token.refresh.used"));
        }

        token.setUsed(true);
        refreshTokenRepository.save(token);

        User user = userRepository.findById(token.getUser().getId())
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));

        String jwt = jwtService.generateToken(user);
        String encodedToken = TokenUtils.hashedToken(jwt);

        RefreshToken newToken = createRefreshToken(user);

        user.setActiveToken(encodedToken);
        userRepository.save(user);

        return new AuthResponse(jwt, newToken.getRawToken());
    }

    @Scheduled(cron = "0 0 3 * * ?") // Runs daily at 3 AM
    @Transactional
    public void cleanOldRefreshTokens() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
        refreshTokenRepository.deleteAllExpiredOrUsedBefore(cutoff);
    }

}