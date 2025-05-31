package com.project.spring_project.controller.user;

import com.project.spring_project.dto.request.*;
import com.project.spring_project.dto.response.AuthResponse;
import com.project.spring_project.service.user.AuthService;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.service.user.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final LocalizationService localizationService;

    /**
     * Public endpoint for user login
     *
     * @param request the login request containing email and password
     * @return a response entity containing the authentication response
     */
    @Operation(summary = "User login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            links = {
                                    @Link(name = "Refresh Token", operationId = "refreshToken", description = "Use the refresh token to get a new access token"),
                                    @Link(name = "Password Reset", operationId = "resetPassword", description = "Reset your password using the token sent to your email")
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @ApiResponse(responseCode = "423", description = "Account locked")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Public endpoint for user registration
     *
     * @param request the registration request containing user details
     * @return a response entity indicating successful registration
     */
    @PostMapping("/register")
    @Operation(summary = "User registration",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registration successful",
                            links = {
                                    @Link(name = "Login", operationId = "login", description = "Login with your new account")
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "400", description = "Email already exists"),
                    @ApiResponse(responseCode = "400", description = "Username already exists"),
                    @ApiResponse(responseCode = "400", description = "Invalid email format"),
                    @ApiResponse(responseCode = "400", description = "Invalid password format")
            }
    )
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(localizationService.get("user.registered"));
    }

    /**
     * Public endpoint for requesting a password reset, which sends a reset email to the user or a code to reset the password
     *
     * @param request the request containing the email address for password reset and whether to send a code or an email
     * @return a response entity indicating that the password reset email has been sent
     */
    @Operation(summary = "Request password reset email link or code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset email sent",
                            links = {
                                    @Link(name = "Password Reset", operationId = "resetPassword", description = "Reset your password using the token sent to your email")
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Error sending email")
            }
    )
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestReset(@Valid @RequestBody EmailRequest request) {
        authService.requestPasswordReset(request.getEmail(), request.isSendAsCode());
        return ResponseEntity.ok(localizationService.get("user.password.reset.sent"));
    }

    /**
     * Public endpoint for resetting the password
     *
     * @param request the request containing the token and new password
     * @return a response entity indicating successful password reset
     */
    @Operation(summary = "Reset password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successful",
                            links = {
                                    @Link(name = "Login", operationId = "login", description = "Login with your new password")
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid token"),
                    @ApiResponse(responseCode = "400", description = "Token has expired"),
                    @ApiResponse(responseCode = "400", description = "Token has already been used"),
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(localizationService.get("user.password.reset"));
    }

    /**
     * Public endpoint for refreshing the access token
     *
     * @param request the request containing the refresh token
     * @return a response entity containing the new access token
     */
    @Operation(summary = "Refresh access token, use the refreshToken to get a new access token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
                    @ApiResponse(responseCode = "400", description = "Token has expired"),
                    @ApiResponse(responseCode = "400", description = "Token has already been used"),
                    @ApiResponse(responseCode = "400", description = "User not found"),
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

}