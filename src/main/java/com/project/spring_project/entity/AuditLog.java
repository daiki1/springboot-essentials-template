package com.project.spring_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "operation")
    private String operation; // e.g., "LOGIN", "ROLE_CHANGE", "USER_DELETION"

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "details")
    private String details; // Any additional info, e.g., the old and new roles, IP address

}