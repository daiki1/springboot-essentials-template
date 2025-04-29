package com.project.spring_project.payload.request;

import com.project.spring_project.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @ValidPassword(message = "Password must be 8-50 characters long, contain uppercase, lowercase, number and special character")
    private String password;

    @Email
    @NotBlank
    private String email;
}