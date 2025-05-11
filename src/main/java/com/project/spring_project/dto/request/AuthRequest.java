package com.project.spring_project.dto.request;

import com.project.spring_project.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an authentication request containing user credentials.
 * <p>
 * This class is used to encapsulate the username and password provided by the user during authentication.
 */
@Getter
@Setter
public class AuthRequest {
    @NotBlank
    private String username;

    @ValidPassword
    private String password;

}