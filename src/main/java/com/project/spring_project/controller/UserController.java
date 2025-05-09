package com.project.spring_project.controller;

import com.project.spring_project.dto.UserDto;
import com.project.spring_project.dto.request.ChangeRolesRequest;
import com.project.spring_project.dto.request.UserStatusUpdateRequest;
import com.project.spring_project.dto.request.UserUpdateRequest;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LocalizationService localizationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public Page<UserDto> listUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/by_name/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public UserDto getUserByName(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/by_email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public UserDto getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) throws Exception {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @PatchMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestBody UserStatusUpdateRequest request) {
        userService.updateUserStatus(id, request.isActive());
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeRoles(@PathVariable Long userId, @RequestBody @Valid ChangeRolesRequest request) {
        userService.updateUserRoles(userId, request.getRoles());
        return ResponseEntity.ok(localizationService.get("user.role.changed"));
    }

    @PutMapping("/language")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeLanguage(@RequestBody Map<String, String> request) {
        String language = request.get("language");
        userService.changeLanguage(language);
        return ResponseEntity.ok(localizationService.get("user.language.updated"));
    }
}
