package com.project.spring_project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request to send an email.
 * <p>
 * This class is used to encapsulate the email address that needs to be validated and sent.
 */
@Getter
@Setter
public class EmailRequest {
    @Email
    @NotBlank
    private String email;

    private boolean sendAsCode = false;
}
