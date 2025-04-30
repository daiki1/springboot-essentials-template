package com.project.spring_project.utils;

import com.project.spring_project.payload.request.RegisterRequest;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    public void registerUserIfNotExists(){
        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(testUsername);
            request.setPassword(rawPassword);
            request.setEmail(email);

            authService.register(request);
        } catch (Exception e) {
            // User already exists, from previous test
        }
    }

    public void deleteTestUser() {
        authService.deleteTestUser(testUsername);
    }

}
