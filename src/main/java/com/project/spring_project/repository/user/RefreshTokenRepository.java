package com.project.spring_project.repository.user;

import com.project.spring_project.entity.user.RefreshToken;
import com.project.spring_project.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface  RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserOrderByExpiryDateDesc(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.used = true OR r.expiryDate < :cutoff")
    void deleteAllExpiredOrUsedBefore(@Param("cutoff") Instant cutoff);

    List<RefreshToken> findAllByUser(User user);

    void deleteByUser(User user);
}