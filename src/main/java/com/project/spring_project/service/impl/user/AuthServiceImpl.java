package com.project.spring_project.service.impl.user;

import com.project.spring_project.entity.user.PasswordResetToken;
import com.project.spring_project.entity.user.RefreshToken;
import com.project.spring_project.entity.user.Role;
import com.project.spring_project.entity.user.User;
import com.project.spring_project.exception.EmailSendException;
import com.project.spring_project.dto.request.AuthRequest;
import com.project.spring_project.dto.request.RegisterRequest;
import com.project.spring_project.dto.response.AuthResponse;
import com.project.spring_project.repository.user.PasswordResetTokenRepository;
import com.project.spring_project.repository.user.RefreshTokenRepository;
import com.project.spring_project.repository.user.RoleRepository;
import com.project.spring_project.repository.user.UserRepository;
import com.project.spring_project.secutrity.services.PasswordService;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.service.AuditLogService;
import com.project.spring_project.service.user.AuthService;
import com.project.spring_project.service.EmailService;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
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
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final PasswordService passwordService;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    private final LocalizationService localizationService;

    /**
     * This method is used to authenticate a user and generate a JWT token.
     *
     * @param request the authentication request containing username/email and password
     * @return AuthResponse containing the JWT token and refresh token
     */
    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(localizationService.get("user.not.found")));

        if (user.isAccountLocked()) {
            if (user.getLockTime() != null && user.getLockTime().isBefore(LocalDateTime.now().minusMinutes(15))) {
                // Unlock after timeout
                user.setAccountLocked(false);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            } else {

                auditLogService.logAudit(user, "ACCOUNT LOCKED", "Account is locked.");
                throw new LockedException(localizationService.get("exception.user.account.locked"));
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

            RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(user);

            auditLogService.logAudit(user, "LOGIN", "User successfully logged in.");
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

    /**
     * This method is used to register a new user.
     *
     * @param request the registration request containing user details
     */
    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(localizationService.get("user.name.already.taken"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(localizationService.get("user.email.already.registered"));
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException(localizationService.get("user.role.not.found", "USER")));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordService.encodePassword(request.getPassword())); // 👈 Hash the password
        user.setEnabled(true); // or false if you require email verification
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }

    /**
     * This method is used to request a password reset. It generates a token or a code and sends it to the user's email.
     *
     * @param email the email address of the user requesting the password reset
     */
    @Override
    public void requestPasswordReset(String email, boolean sendAsCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.email.not.found", email)));

        Optional<PasswordResetToken> existing = passwordResetTokenRepository.findByUserId(user.getId());
        existing.ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        if (sendAsCode) {
            //Send a n-digit code instead of a link
            token = TokenUtils.generateRandomCode(5);
            expiry = LocalDateTime.now().plusMinutes(5); // Shorter expiry for code
        }

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);

        passwordResetTokenRepository.save(resetToken);

        // For now: log it. In prod, send email.
        String message = sendAsCode
                ? localizationService.get("user.password.code.message", token)
                : localizationService.get("user.password.link.message", "https://your-app.com/reset-password?token=" + token);
        System.out.println(message);
        try {
            emailService.sendPlainTextEmail(
                    user.getEmail(),
                    localizationService.get("user.password.request"),
                    message
            );
        } catch (MailException e) {
            throw new EmailSendException("exception.email.send.failed", e);
        }
    }

    /**
     * This method is used to reset the password using the provided token.
     *
     * @param token       the password reset token
     * @param newPassword the new password
     */
    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(localizationService.get("token.expired"));
        }

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException(localizationService.get("token.already.used"));
        }

        User user = resetToken.getUser();
        user.setPassword(passwordService.encodePassword(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    /**
     * This method is used to delete a test user.
     *
     * @param username the username of the test user to be deleted
     */
    @Override
    @Transactional
    public void deleteTestUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        Long userId = user != null ? user.getId() : null;
        if (user != null) {
            passwordResetTokenRepository.deleteByUserId(user.getId());
            refreshTokenRepository.deleteByUser(user);
            userRepository.delete(user);
        }
        auditLogService.logAudit(user, "USER_DELETION", "User deleted: " + username);

    }


}