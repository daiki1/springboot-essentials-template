package com.project.spring_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Configures CORS settings for the application.
     * <p>
     * This method sets up allowed origins, methods, headers, and other CORS-related configurations.
     *
     * @return a CorsConfigurationSource object with the configured CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                //"http://localhost:4200",         // Angular/React dev
                //"http://yourdomain.com",         // Same server
                //"http://another-frontend.com",   // External server
                //"*"                              // Allow all origins for testing purposes
        ));
        //You can test if its working by using the following command and uncommenting the line above "http://yourdomain.com"
        // curl -H "Origin: http://yourdomain.com" --verbose http://localhost:8080/api/test/all

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache preflight requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}