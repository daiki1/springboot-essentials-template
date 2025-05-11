package com.project.spring_project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request to update the status of a user.
 * <p>
 * This class is used to encapsulate the active status of a user.
 */
@Getter
@Setter
public class UserStatusUpdateRequest {

    @NotNull(message = "{validation.active.required}")
    private Boolean active;
}
