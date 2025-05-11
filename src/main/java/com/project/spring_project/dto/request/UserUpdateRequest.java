package com.project.spring_project.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request to update user information.
 * <p>
 * This class is used to encapsulate the fields that can be updated for a user.
 */
@Getter
@Setter
public class UserUpdateRequest {
    private String username;

    @Email
    private String email;

    //Add extra fields to update
}
