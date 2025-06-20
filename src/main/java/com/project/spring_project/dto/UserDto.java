package com.project.spring_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents a Data Transfer Object (DTO) for user information.
 * <p>
 * This class is used to encapsulate user-related data that is transferred between the server and client.
 */
@Getter
@Setter
public class UserDto {
    private Long userId;
    private String username;
    private String email;
    private boolean active;
    private Set<String> roles;
    private boolean locked;
    private LocalDateTime lockedTime;
    private String language;
    private LocalDateTime createdAt;

}
