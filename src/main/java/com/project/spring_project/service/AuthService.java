package com.project.spring_project.service;

import com.project.spring_project.entity.PasswordResetToken;
import com.project.spring_project.entity.RefreshToken;
import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import com.project.spring_project.payload.request.AuthRequest;
import com.project.spring_project.payload.request.RegisterRequest;
import com.project.spring_project.payload.response.AuthResponse;
import com.project.spring_project.repository.PasswordResetTokenRepository;
import com.project.spring_project.repository.RefreshTokenRepository;
import com.project.spring_project.repository.RoleRepository;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.secutrity.services.PasswordService;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordService passwordService;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isAccountLocked()) {
            if (user.getLockTime() != null && user.getLockTime().isBefore(LocalDateTime.now().minusMinutes(15))) {
                // Unlock after timeout
                user.setAccountLocked(false);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            } else {
                auditLogService.logAudit(user.getUsername(), "ACCOUNT LOCKED", "Account is locked.");
                throw new LockedException("Account is locked. Please try again later.");
            }
        }

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtService.generateToken(authentication);

            //CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            //User user = userDetails.getUser();
            String encodedToken = TokenUtils.hashedToken(token);

            user.setActiveToken(encodedToken);
            user.setFailedAttempts(0);
            user.setAccountLocked(false);
            user.setLockTime(null);
            userRepository.save(user);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            auditLogService.logAudit(user.getUsername(), "LOGIN", "User successfully logged in.");
            return new AuthResponse(token, refreshToken.getRawToken());
        } catch (BadCredentialsException ex) {
            int newFailAttempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(newFailAttempts);

            if (newFailAttempts >= 5) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
            }

            userRepository.save(user);

            throw ex; // rethrow to controller/global exception handler
        }
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordService.encodePassword(request.getPassword())); // 👈 Hash the password
        user.setEnabled(true); // or false if you require email verification
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }

    @Transactional
    public void changeUserRole(String username, List<String> newRoles) {
        // Get user and change role logic here
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = new HashSet<>();
        for (String newRole: newRoles){
            Role role = roleRepository.findByName(newRole)
                    .orElseThrow(() -> new IllegalStateException("Role "+newRole+" not found"));
            roles.add(role);
        }
        Set<Role> oldRoles = user.getRoles();
        user.setRoles(roles);
        userRepository.save(user);

        // Log the role change
        auditLogService.logAudit(username, "ROLE_CHANGE", "Role changed from " + oldRoles.toString() + " to " + roles.toString());
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<PasswordResetToken> existing = passwordResetTokenRepository.findByUserId(user.getId());
        existing.ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);

        passwordResetTokenRepository.save(resetToken);

        // For now: log it. In prod, send email.
        System.out.println("Password reset link: https://your-app.com/reset-password?token=" + token);
        emailService.sendPlainTextEmail(
                user.getEmail(),
                "Password Reset Request",
                "Click the link to reset your password: https://your-app.com/reset-password?token=" + token
        );
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token already used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordService.encodePassword(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // This method is used in the test class to delete the test user
    @Transactional
    public void deleteTestUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            passwordResetTokenRepository.deleteByUserId(user.getId());
            refreshTokenRepository.deleteByUser(user);
            userRepository.delete(user);
        }
        auditLogService.logAudit(username, "USER_DELETION", "User deleted: " + username);

    }
}