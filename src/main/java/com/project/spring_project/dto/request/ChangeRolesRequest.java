package com.project.spring_project.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a request to change user roles.
 * <p>
 * This class is used to encapsulate the list of roles that need to be assigned to a user.
 */
@Getter
@Setter
public class ChangeRolesRequest {
    @NotEmpty
    private List<String> roles;
}
