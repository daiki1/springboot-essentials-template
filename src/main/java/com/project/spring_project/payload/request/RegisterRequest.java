package com.project.spring_project.payload.request;

import com.project.spring_project.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @ValidPassword(message = "{password.invalid}")
    private String password;

    @Email
    @NotBlank
    private String email;
}