package com.project.spring_project.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a response containing authentication tokens.
 * <p>
 * This class is used to encapsulate the JWT token and refresh token returned after successful authentication.
 */
@Getter
@Setter
public class AuthResponse {
    private String token;
    private String refreshToken;


    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }


}