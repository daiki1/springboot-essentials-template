package com.project.spring_project.secutrity.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring_project.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.spring_project.util.ErrorResponseUtil.buildErrorResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method is called when an exception is thrown due to an unauthenticated user trying to access a protected resource.
     * <p>
     * It sets the response status to 401 (Unauthorized) and returns a JSON error message.
     *
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param authException the AuthenticationException that was thrown
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Check for our custom attribute
        Exception exception = (Exception) request.getAttribute("exception");
        String message = authException.getMessage();

        if (exception instanceof JwtAuthenticationException) {
            message = exception.getMessage();
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(
                new ObjectMapper().writeValueAsString(
                    buildErrorResponse(HttpStatus.UNAUTHORIZED, message)
                )
        );
    }
}