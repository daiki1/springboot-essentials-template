package com.project.spring_project.exception;

/**
 * Custom exception class to handle bad request scenarios.
 * <p>
 * This exception is thrown when a request is invalid or cannot be processed due to client-side errors.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}