package com.project.spring_project.repository.user;

import com.project.spring_project.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    void deleteByUsername(String testUsername);

    @Query("SELECT u FROM User u WHERE u.username = :value OR LOWER(u.email) = LOWER(:value)")
    Optional<User> findByUsernameOrEmail(@Param("value") String usernameOrEmail);
}
