package com.project.spring_project.payload.request;

import com.project.spring_project.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;

    @ValidPassword
    private String newPassword;

}