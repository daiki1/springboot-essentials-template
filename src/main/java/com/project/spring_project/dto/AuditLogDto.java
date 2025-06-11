package com.project.spring_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditLogDto {
    private Long id;
    private String operation;
    private LocalDateTime timestamp;
    private String details;
    private String resource;
    private String ipAddress;
    private Long userId;
    private String username;
}