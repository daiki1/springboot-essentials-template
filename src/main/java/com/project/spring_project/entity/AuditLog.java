package com.project.spring_project.entity;

import com.project.spring_project.entity.user.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "operation")
    private String operation; // e.g., "LOGIN", "ROLE_CHANGE", "USER_DELETION"

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "details")
    private String details; // Any additional info, e.g., the old and new roles, IP address

    private String resource;

    @Column(name = "ip_address")
    private String ipAddress;
}