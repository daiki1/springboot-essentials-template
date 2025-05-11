package com.project.spring_project.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception class to handle JWT authentication scenarios.
 * <p>
 * This exception is thrown when there is an issue with JWT authentication.
 */
public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String message) {
        super(message);
    }
}