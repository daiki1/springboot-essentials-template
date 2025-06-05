package com.project.spring_project.secutrity.jwt;

import com.project.spring_project.exception.JwtAuthenticationException;
import com.project.spring_project.secutrity.services.CustomUserDetails;
import com.project.spring_project.secutrity.services.UserDetailsServiceImpl;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.util.TokenUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {
    @Value("${app.oneSingleSignOn}")
    private boolean oneSingleSignOn;

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final LocalizationService localizationService;

    /**
     * This method is called for every request to check if the request contains a valid JWT token.
     * If the token is valid, it sets the authentication in the security context.
     *
     * @param request  the servlet request
     * @param response the servlet response
     * @param chain    the filter chain
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    /*@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String jwt = getJwtFromRequest((HttpServletRequest) request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromJWT(jwt);
            CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String activeToken = userDetails.getUser().getActiveToken();
            if(oneSingleSignOn && (activeToken==null || !TokenUtils.hashedToken(jwt).equals(userDetails.getUser().getActiveToken()))){//!EncoderUtil.matches(jwt, userDetails.getUser().getActiveToken())
                SecurityContextHolder.clearContext();
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                authenticationEntryPoint.commence(httpRequest, httpResponse, new JwtAuthenticationException(localizationService.get("exception.invalid.expired.session")));
                return; // STOP filter chain
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }*/
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String jwt = getJwtFromRequest(httpRequest);

            if (!StringUtils.hasText(jwt)) {
                chain.doFilter(request, response); // No token = proceed (possibly to public endpoint)
                return;
            }

            // Token is present — validate it
            if (!tokenProvider.validateToken(jwt)) {
                // Token invalid or expired — respond with 401
                authenticationEntryPoint.commence(
                        httpRequest,
                        httpResponse,
                        new JwtAuthenticationException(localizationService.get("exception.invalid.expired.session"))
                );
                return;
            }

            // Token is valid — load user
            String username = tokenProvider.getUsernameFromJWT(jwt);
            CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Optional: One-session check
            String activeToken = userDetails.getUser().getActiveToken();
            if (oneSingleSignOn &&
                    (activeToken == null || !TokenUtils.hashedToken(jwt).equals(activeToken))) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(
                        httpRequest,
                        httpResponse,
                        new JwtAuthenticationException(localizationService.get("exception.invalid.expired.session"))
                );
                return;
            }

            // Authenticated successfully
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtAuthenticationException ex) {
            authenticationEntryPoint.commence(httpRequest, httpResponse, ex);
            return;
        } catch (Exception ex) {
            authenticationEntryPoint.commence(
                    httpRequest, httpResponse,
                    new JwtAuthenticationException("Unexpected error during authentication: " + ex.getMessage())
            );
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the request header.
     *
     * @param request the servlet request
     * @return the JWT token or null if not found
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}