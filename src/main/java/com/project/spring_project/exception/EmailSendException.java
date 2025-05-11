package com.project.spring_project.exception;

/**
 * Custom exception class to handle Email sending scenarios.
 * <p>
 * This exception is thrown when a request is invalid or cannot be processed due to client-side errors.
 */
public class EmailSendException extends RuntimeException {
    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}