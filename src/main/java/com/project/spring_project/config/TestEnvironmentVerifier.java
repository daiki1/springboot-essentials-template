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

    /**
     * Verifies that the application is not running in a production environment.
     * <p>
     * This method checks the active profiles and throws an exception if any of them indicate a production environment.
     */
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
