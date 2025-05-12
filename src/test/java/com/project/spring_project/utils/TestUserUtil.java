package com.project.spring_project.utils;

import com.project.spring_project.entity.user.PasswordResetToken;
import com.project.spring_project.entity.user.User;
import com.project.spring_project.dto.request.RegisterRequest;
import com.project.spring_project.repository.user.PasswordResetTokenRepository;
import com.project.spring_project.repository.user.RoleRepository;
import com.project.spring_project.repository.user.UserRepository;
import com.project.spring_project.secutrity.services.PasswordService;
import com.project.spring_project.service.user.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Getter
@RequiredArgsConstructor
public class TestUserUtil {

    private final String testUsername = "testuser_121_unitTest_unique_username39";
    private final String rawPassword = "TestPassword123!";
    private final String email = "testuser_121_unitTest_unique_username39@example.com";

    private final String requestBody = "{\"username\": \""+testUsername+"\", \"password\": \""+rawPassword+"\"}";

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordService passwordService;

    public void registerUserIfNotExists() {
        registerUserIfNotExists(false);

    }
    public void registerUserIfNotExists(boolean throwException) {
        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(testUsername);
            request.setPassword(rawPassword);
            request.setEmail(email);

            authService.register(request);
        } catch (Exception e) {
            if (throwException) {
                throw e;
            }
        }
    }

    public void deleteTestUser() {
        authService.deleteTestUser(testUsername);
    }

    public Optional<User> fingUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deletePasswordResetToken(Long userId) {
        passwordResetTokenRepository.deleteByUserId(userId);
    }

    public Optional<PasswordResetToken> findRefreshTokensByUser(Long userId) {
        return passwordResetTokenRepository.findByUserId(userId);
    }
}
