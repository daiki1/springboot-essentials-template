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
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
}
