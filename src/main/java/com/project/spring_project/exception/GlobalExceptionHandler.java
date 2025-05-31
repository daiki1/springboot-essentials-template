package com.project.spring_project.exception;

import com.project.spring_project.SpringProjectApplication;
import com.project.spring_project.util.LocalizationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.stream.Collectors;

import static com.project.spring_project.util.ErrorResponseUtil.buildErrorResponse;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final LocalizationService localizationService;

    private static final Logger logger = LoggerFactory.getLogger(SpringProjectApplication.class);

    /**
     * Logs error messages based on the active profile.
     * <p>
     * This method checks if the active profile is "dev" and logs the error message accordingly.
     *
     * @param exception the exception type
     * @param message   the error message
     */
    private void logError(String exception, String message) {
        if ("dev".equalsIgnoreCase(activeProfile))
            logger.error("{}: {}", exception, message);
    }

    /**
     * Handles IllegalArgumentException and returns a 400 Bad Request response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the IllegalArgumentException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        logError("IllegalArgumentException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles validation exceptions and returns a 400 Bad Request response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the validation exception
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class, HandlerMethodValidationException.class })
    public ResponseEntity<Object> handleValidationExceptions(Exception ex) {

        String errorMessage = localizationService.get("validation.failed");

        if (ex instanceof MethodArgumentNotValidException manvEx) {
            /*errorMessage = manvEx.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining("; "));*/
            errorMessage = manvEx.getBindingResult().getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof HandlerMethodValidationException hmveEx) {
            errorMessage = hmveEx.getAllErrors().stream()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        }

        logError("handleValidationExceptions", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle malformed JSON request and returns a 400 Bad Request response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the HttpMessageNotReadableException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        logError("HttpMessageNotReadableException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST,localizationService.get("exception.malformed.json"))
                , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle email send exception and returns a 400 Bad Request response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the EmailSendException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<Map<String, Object>> handleEmailSendException(EmailSendException ex) {
        logError("HttpMessageNotReadableException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST,localizationService.get("exception.email.send.failed"))
                , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violation exception and returns a 400 Bad Request response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the ConstraintViolationException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        logError("handleConstraintViolation", errorMessage);
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST,errorMessage)
                , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle bad request exception and returns a 400 Bad Request response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the BadRequestException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        logError("BadRequestException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, localizationService.get(ex.getMessage())),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle username not found exception and returns a 401 Unauthorized response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the UsernameNotFoundException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex) {
        logError("UsernameNotFoundException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, localizationService.get("exception.invalid.credentials")),
                HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Handle bad credentials exception and returns a 401 Unauthorized response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the BadCredentialsException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        logError("BadCredentialsException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, localizationService.get("exception.invalid.credentials")),
                HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Handle authentication exception and returns a 401 Unauthorized response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the AuthenticationException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        logError("AuthenticationException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, localizationService.get("exception.invalid.auth.credentials"))
                , HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle access denied exception and returns a 403 Forbidden response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the AccessDeniedException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        logError("AccessDeniedException", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(HttpStatus.FORBIDDEN, localizationService.get("exception.access.permission.denied"));
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle not found exception and returns a 404 Not Found response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the NoHandlerFoundException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNotFound(NoHandlerFoundException ex) {
        logError("NoHandlerFoundException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.NOT_FOUND, localizationService.get("exception.endoint.not.found",ex.getRequestURL())),
                HttpStatus.NOT_FOUND);
    }

    /**
     * Handle method not allowed exception and returns a 405 Method Not Allowed response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the HttpRequestMethodNotSupportedException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        logError("HttpRequestMethodNotSupportedException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, localizationService.get("exception.method.not.allowed")),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle locked exception and returns a 423 Locked response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the LockedException
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLockedException(LockedException ex) {
        logError("LockedException", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.LOCKED, localizationService.get("exception.user.account.locked")),
                HttpStatus.LOCKED // HTTP 423 Locked
        );
    }

    /**
     * Handle all other exceptions and returns a 500 Internal Server Error response.
     * <p>
     * This method logs the error message and returns a response entity with the error details.
     *
     * @param ex the Exception
     * @return a ResponseEntity with the error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex) {
        logError("Exception", ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,localizationService.get("exception.general"))
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}