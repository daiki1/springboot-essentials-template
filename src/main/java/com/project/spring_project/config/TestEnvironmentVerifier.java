package com.project.spring_project.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
public class TestEnvironmentVerifier {
    private final Environment environment;

    public TestEnvironmentVerifier(Environment environment) {
        this.environment = environment;
    }

    // This method will be called after the Spring context is initialized
    // and will check the active profiles to ensure that tests are not run in production
    @PostConstruct
    public void verifyEnvironment() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (Arrays.stream(activeProfiles).anyMatch(profile ->
                profile.equalsIgnoreCase("prod") || profile.equalsIgnoreCase("production"))) {
            throw new IllegalStateException("Tests should not run in production environment!");
        }
        System.out.println("Environment check passed. Active profiles: " + Arrays.toString(activeProfiles));
    }
}
