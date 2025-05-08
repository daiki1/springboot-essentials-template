package com.project.spring_project.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.spring_project.dto.UserDto;
import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import com.project.spring_project.mapper.UserMapper;
import com.project.spring_project.payload.request.UserUpdateRequest;
import com.project.spring_project.repository.RoleRepository;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.service.AuditLogService;
import com.project.spring_project.service.LocalizationService;
import com.project.spring_project.service.UserService;
import com.project.spring_project.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LocalizationService localizationService;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));

        String oldUserJson = JsonUtils.objectToJson(user);

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        auditLogService.logAudit(user.getId(), "UPDATE_USER", "Update user from " + oldUserJson + " to " + JsonUtils.objectToJson(user));

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));
        user.setEnabled(false);
        userRepository.save(user);

    }

    @Override
    public void updateUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));
        user.setEnabled(active);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<String> newRoles) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));

        Set<Role> roles = new HashSet<>();
        for (String newRole: newRoles){
            Role role = roleRepository.findByName(newRole)
                    .orElseThrow(() -> new IllegalStateException(localizationService.get("user.role.not.found", newRole)));
            roles.add(role);
        }

        String oldRolesJson = JsonUtils.objectToJson(user.getRoles());

        user.setRoles(roles);
        userRepository.save(user);

        // Log the role change
        auditLogService.logAudit(user.getId(), "ROLE_CHANGE", "Role changed from " + oldRolesJson + " to " + JsonUtils.objectToJson(roles));
    }

    @Override
    public void changeLanguage(String language) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(localizationService.get("user.not.found")));
        user.setLanguage(language);
        userRepository.save(user);

    }
}
