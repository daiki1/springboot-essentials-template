package com.project.spring_project.repository;

import com.project.spring_project.entity.RefreshToken;
import com.project.spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface  RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.used = true OR r.expiryDate < :cutoff")
    void deleteAllExpiredOrUsedBefore(@Param("cutoff") Instant cutoff);

    List<RefreshToken> findAllByUser(User user);

    void deleteByUser(User user);
}