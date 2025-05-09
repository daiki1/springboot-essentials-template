package com.project.spring_project.dto.request;

import com.project.spring_project.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    @NotBlank
    private String username;

    @ValidPassword
    private String password;

}