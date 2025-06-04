package com.project.spring_project.dto.request;

import com.project.spring_project.validation.NotEmail;
import com.project.spring_project.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Represents a request to register a new user.
 * <p>
 * This class is used to encapsulate the username, password, and email address provided by the user during registration.
 */
@Data
public class RegisterRequest {

    @NotBlank
    @NotEmail
    private String username;

    @ValidPassword
    private String password;

    @Email
    @NotBlank
    private String email;
}