package com.project.spring_project.dto.request;

import com.project.spring_project.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request to reset a user's password.
 * <p>
 * This class is used to encapsulate the token and the new password provided by the user during the password reset process.
 */
@Getter
@Setter
public class ResetPasswordRequest {
    private String token;

    @ValidPassword
    private String newPassword;

}