package com.project.spring_project.secutrity.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring_project.secutrity.services.RateLimitingService;
import com.project.spring_project.util.LocalizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.project.spring_project.util.ErrorResponseUtil.buildErrorResponse;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;
    private final LocalizationService localizationService;

    /**
     * Retrieves the client's IP address from the request.
     * <p>
     * This method checks for the "X-Forwarded-For" header to get the original client IP
     * when the application is behind a proxy or load balancer.
     *
     * @param request the HttpServletRequest object
     * @return the client's IP address as a String
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim(); // Use the first IP in the chain
        }
        return request.getRemoteAddr();
    }

    /**
     * Filters incoming requests to apply rate limiting.
     * <p>
     * This method checks if the request URI starts with "/api/auth/" and applies rate limiting
     * based on the client's IP address. If the limit is exceeded, a 429 Too Many Requests response
     * is sent back to the client.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param filterChain the FilterChain object for further processing
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/auth/")) {
            //String clientIp = request.getRemoteAddr(); // or use a header like "X-Forwarded-For" if behind proxy
            String clientIp = getClientIp(request);
            boolean allowed = rateLimitingService.tryConsume(clientIp);

            if (!allowed) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");

                Map<String, Object> errorBody = buildErrorResponse(
                        HttpStatus.TOO_MANY_REQUESTS,
                        localizationService.get("exception.too.many.requests")
                );

                String json = new ObjectMapper().writeValueAsString(errorBody);
                response.getWriter().write(json);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}