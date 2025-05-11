package com.project.spring_project.controller;

import com.project.spring_project.dto.UserDto;
import com.project.spring_project.dto.request.ChangeRolesRequest;
import com.project.spring_project.dto.request.UserStatusUpdateRequest;
import com.project.spring_project.dto.request.UserUpdateRequest;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.service.UserService;
import com.project.spring_project.util.SupportedLanguages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final LocalizationService localizationService;

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable the pagination information
     * @return a paginated list of UserDto objects
     */
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public Page<UserDto> listUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to be retrieved
     * @return the UserDto object containing user information
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to be retrieved
     * @return the UserDto object containing user information
     */
    @Operation(summary = "Get user by username", description = "Retrieves a user by their username.",
        responses = {
                @ApiResponse(responseCode = "200", description = "User found"),
                @ApiResponse(responseCode = "404", description = "User not found"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public UserDto getUserByName(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to be retrieved
     * @return the UserDto object containing user information
     */
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "400", description = "Invalid email format"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public UserDto getUserByEmail(@PathVariable @Email(message = "{validation.email.invalid}") String email) {
        return userService.getUserByEmail(email);
    }

    /**
     * Updates user information based on the provided ID and request body.
     *
     * @param id      the ID of the user to be updated
     * @param request the request body containing updated user information
     * @return the updated UserDto object
     */
    @Operation(summary = "Update user", description = "Updates user information based on the provided ID and request body.",
        responses = {
                @ApiResponse(responseCode = "200", description = "User updated successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid user ID or request body"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) throws Exception {
        return userService.updateUser(id, request);
    }

    /**
     * Deactivates a user by setting their status to inactive.
     *
     * @param id the ID of the user to be deactivated
     */
    @Operation(summary = "Deactivate user", description = "Deactivates a user by setting their status to inactive.",
        responses = {
                @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid user ID provided"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    /**
     * Updates the status of a user (active/inactive).
     *
     * @param id      the ID of the user whose status is to be updated
     * @param request the request body containing the new status (e.g., {"active": true})
     */
    @Operation(summary = "Change user status", description = "Updates the status of a user (active/inactive).",
        responses = {
                @ApiResponse(responseCode = "200", description = "Status changed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid status provided"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusUpdateRequest request) {
        userService.updateUserStatus(id, request.getActive());
    }

    /**
     * Updates the roles of a user.
     *
     * @param userId  the ID of the user whose roles are to be updated
     * @param request  the request body containing the new roles as a list (e.g., ["USER", "ADMIN"])
     * @return ResponseEntity with status and message
     */
    @Operation(summary = "Change user roles", description = "Updates the roles of a user.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Roles changed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid role(s) provided"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeRoles(@PathVariable Long userId, @RequestBody @Valid ChangeRolesRequest request) {
        userService.updateUserRoles(userId, request.getRoles());
        return ResponseEntity.ok(localizationService.get("user.role.changed"));
    }

    /**
     * Changes the language preference for the currently authenticated user.
     *
     * @param request the request body containing the new language code  (e.g., "en", "es")
     * @return ResponseEntity with status and message
     */
    @Operation(summary = "Change user language", description = "Changes the language preference for the currently authenticated user.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Language changed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid language code"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @PutMapping("/language")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeLanguage(@RequestBody Map<String, String> request) {
        String language = request.get("language");

        if (language == null || !SupportedLanguages.SUPPORTED_LANGUAGES.contains(language)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", localizationService.get("user.language.not.supported", SupportedLanguages.SUPPORTED_LANGUAGES)));
        }

        userService.changeLanguage(language);
        return ResponseEntity.ok(localizationService.get("user.language.updated"));
    }
}
