package com.project.spring_project.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request to refresh a token.
 * <p>
 * This class is used to encapsulate the refresh token provided by the user during the token refresh process.
 */
@Getter
@Setter
public class RefreshTokenRequest {
    private String refreshToken;
}
