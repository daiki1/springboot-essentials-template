package com.project.spring_project.util;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponseUtil {

    /**
     * Builds a standard error response.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @return a map containing the error response
     */
    public static Map<String, Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }

}
