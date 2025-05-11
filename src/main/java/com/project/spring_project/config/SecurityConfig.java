package com.project.spring_project.config;

import com.project.spring_project.secutrity.filter.RateLimitingFilter;
import com.project.spring_project.secutrity.jwt.JwtAuthenticationEntryPoint;
import com.project.spring_project.secutrity.jwt.JwtAuthenticationFilter;
import com.project.spring_project.secutrity.services.PasswordService;
import com.project.spring_project.secutrity.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method sets up CORS, CSRF, session management, and request authorization.
     *
     * @param http the HttpSecurity object to configure
     * @param corsConfigSource the CorsConfigurationSource object for CORS settings
     * @return a SecurityFilterChain object with the configured security settings
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigSource) throws Exception {
        http
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'")
                        ) //Allows to load resources only for own domain
                        .frameOptions(frame -> frame.sameOrigin()) //Avoid being embedded in an iframe
                        .httpStrictTransportSecurity(hsts -> hsts //Use https and domain/subdomain
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))//Controls how much referrer info is shared
                )
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigSource))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // <== here
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Allowing all requests to public endpoints and control access using PreAuthorize
                        .requestMatchers("/api/test/**").permitAll()
                        // Example to show how to restrict access to specific roles to specific endpoints
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/auditor/**").hasRole("AUDITOR")
                        .requestMatchers("/api/user/**").hasRole("USER")

                        .requestMatchers( //Doc endpoints
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * Configures the authentication provider for the application.
     * <p>
     * This method sets up a DaoAuthenticationProvider with a custom UserDetailsService and PasswordService.
     *
     * @param passwordService the PasswordService for password encoding and matching
     * @param userDetailsService the UserDetailsService for user authentication
     * @return a DaoAuthenticationProvider object configured for authentication
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordService passwordService, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        // Use PasswordService instead of default passwordEncoder
        authProvider.setPasswordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return passwordService.encodePassword(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return passwordService.matches(rawPassword.toString(), encodedPassword);
            }
        });
        return authProvider;
    }

    /**
     * Configures the PasswordEncoder for the application.
     * <p>
     * This method sets up a BCryptPasswordEncoder for password hashing and matching.
     *
     * @return a PasswordEncoder object configured for password encoding
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}