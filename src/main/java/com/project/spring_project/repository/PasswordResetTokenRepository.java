package com.project.spring_project.repository;

import com.project.spring_project.entity.PasswordResetToken;
import com.project.spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserId(Long id);

    void deleteByUserId(Long userId);

}