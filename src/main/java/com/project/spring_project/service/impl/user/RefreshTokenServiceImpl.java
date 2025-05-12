package com.project.spring_project.service.impl.user;

import com.project.spring_project.entity.user.RefreshToken;
import com.project.spring_project.entity.user.User;
import com.project.spring_project.dto.request.RefreshTokenRequest;
import com.project.spring_project.dto.response.AuthResponse;
import com.project.spring_project.exception.BadRequestException;
import com.project.spring_project.repository.user.RefreshTokenRepository;
import com.project.spring_project.repository.user.UserRepository;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.service.user.RefreshTokenService;
import com.project.spring_project.util.TokenUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.project.spring_project.util.TokenUtils.hashedToken;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtService;
    private final UserRepository userRepository;
    private final LocalizationService localizationService;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtService, UserRepository userRepository, LocalizationService localizationService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.localizationService = localizationService;
    }

    /**
     * Generates a new refresh token for the user.
     *
     * @param user The user for whom the refresh token is generated.
     * @return The generated refresh token.
     */
    @Override
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

    /**
     * Validates the refresh token and generates a new access token.
     *
     * @param request The request containing the refresh token.
     * @return The response containing the new access token and refresh token.
     */
    @Override
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        String hashedToken = hashedToken(request.getRefreshToken());
        RefreshToken token = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new BadRequestException(localizationService.get("token.refresh.invalid")));

        if (token.isUsed() || token.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException(localizationService.get("token.refresh.used"));
        }

        token.setUsed(true);
        refreshTokenRepository.save(token);

        User user = userRepository.findById(token.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));

        String jwt = jwtService.generateToken(user);
        String encodedToken = TokenUtils.hashedToken(jwt);

        RefreshToken newToken = createRefreshToken(user);

        user.setActiveToken(encodedToken);
        userRepository.save(user);

        return new AuthResponse(jwt, newToken.getRawToken());
    }

    /**
     * Deletes all refresh tokens associated with the user.
     *
     * @param user The user whose refresh tokens are to be deleted.
     */
    @Override
    @Scheduled(cron = "0 0 3 * * ?") // Runs daily at 3 AM
    @Transactional
    public void cleanOldRefreshTokens() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
        refreshTokenRepository.deleteAllExpiredOrUsedBefore(cutoff);
    }

}