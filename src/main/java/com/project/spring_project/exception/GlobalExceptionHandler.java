package com.project.spring_project.exception;

import com.project.spring_project.SpringProjectApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.project.spring_project.util.ErrorResponseUtil.buildErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private static final Logger logger = LoggerFactory.getLogger(SpringProjectApplication.class);

    // Method to log errors based on the active profile
    private void logError(String exception, String message) {
        if ("dev".equalsIgnoreCase(activeProfile))
            logger.error("{}: {}", exception, message);
    }

    // Handle token expiration
    /*
    This exception is handled in the JwtAuthenticationEntryPoint class
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Object> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        logError("JwtAuthenticationException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage())
                , HttpStatus.UNAUTHORIZED);
    }*/

    //Handle error 400 bad request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        logError("IllegalArgumentException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // Handle error 400 validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logError("MethodArgumentNotValidException", ex.getMessage());
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, errors.toString())
                , HttpStatus.BAD_REQUEST);
    }

    // Handle error 400 malformed JSON request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        logError("HttpMessageNotReadableException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST,"Malformed JSON request.")
                , HttpStatus.BAD_REQUEST);
    }

    // Handle error 401 unauthorized
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex) {
        logError("UsernameNotFoundException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password."),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Handle error 401 unauthorized
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        logError("BadCredentialsException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password."),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Handle error 401 unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        logError("AuthenticationException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid authentication credentials.")
                , HttpStatus.UNAUTHORIZED);
    }

    // Handle error 403 forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        logError("AccessDeniedException", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // Handle error 404 not found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNotFound(NoHandlerFoundException ex) {
        logError("NoHandlerFoundException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.NOT_FOUND, "The requested endpoint was not found. "+ex.getRequestURL()),
                HttpStatus.NOT_FOUND);
    }

    // Handle error 423 locked
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLockedException(LockedException ex) {
        logError("LockedException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.LOCKED, "Your account is locked. Try again later."),
                HttpStatus.LOCKED // HTTP 423 Locked
        );
    }

    // Optional: handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex) {
        logError("Exception", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong.")
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}