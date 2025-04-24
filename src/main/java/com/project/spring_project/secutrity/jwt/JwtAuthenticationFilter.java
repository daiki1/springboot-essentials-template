package com.project.spring_project.secutrity.jwt;

import com.project.spring_project.exception.JwtAuthenticationException;
import com.project.spring_project.secutrity.services.CustomUserDetails;
import com.project.spring_project.secutrity.services.UserDetailsServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends GenericFilter {
    @Value("${app.oneSingleSignOn}")
    private boolean oneSingleSignOn;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(UserDetailsServiceImpl userDetailsService,
                                   JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String jwt = getJwtFromRequest((HttpServletRequest) request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromJWT(jwt);
            CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String activeToken = userDetails.getUser().getActiveToken();
            if(oneSingleSignOn && (activeToken==null || !jwt.equals(userDetails.getUser().getActiveToken()))){
                SecurityContextHolder.clearContext();
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                authenticationEntryPoint.commence(httpRequest, httpResponse, new JwtAuthenticationException("Invalid or expired session"));
                return; // STOP filter chain
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}