package com.project.spring_project.service.impl.user;

import com.project.spring_project.dto.UserDto;
import com.project.spring_project.entity.user.Role;
import com.project.spring_project.entity.user.User;
import com.project.spring_project.mapper.UserMapper;
import com.project.spring_project.dto.request.UserUpdateRequest;
import com.project.spring_project.repository.user.RoleRepository;
import com.project.spring_project.repository.user.UserRepository;
import com.project.spring_project.service.AuditLogService;
import com.project.spring_project.util.LocalizationService;
import com.project.spring_project.service.user.UserService;
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

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable the pagination information
     * @return a paginated list of UserDto objects
     */
    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to be retrieved
     * @return the UserDto object containing user information
     */
    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to be retrieved
     * @return the UserDto object containing user information
     */
    @Override
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to be retrieved
     * @return the UserDto object containing user information
     */
    @Override
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));
    }

    /**
     * Updates user information based on the provided ID and request body.
     *
     * @param id      the ID of the user to be updated
     * @param request the request body containing updated user information
     * @return the updated UserDto object
     */
    @Override
    public UserDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));

        String oldUserJson = JsonUtils.objectToJsonNotNulls(user);

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        auditLogService.logAudit(user, "UPDATE_USER", "Update user from " + oldUserJson + " to " + JsonUtils.objectToJsonNotNulls(request));

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Deactivates a user by setting their status to inactive.
     *
     * @param id the ID of the user to be deactivated
     */
    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));
        user.setEnabled(false);
        userRepository.save(user);

    }

    /**
     * Updates the status of a user (active/inactive).
     *
     * @param id      the ID of the user whose status is to be updated
     * @param active  the new status (true for active, false for inactive)
     */
    @Override
    public void updateUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));
        user.setEnabled(active);
        userRepository.save(user);

    }

    /**
     * Updates the roles of a user.
     *
     * @param userId  the ID of the user whose roles are to be updated
     * @param newRoles  the new roles to be assigned to the user
     */
    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<String> newRoles) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));

        Set<Role> roles = new HashSet<>();
        for (String newRole: newRoles){
            Role role = roleRepository.findByName(newRole)
                    .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.role.not.found", newRole)));
            roles.add(role);
        }

        String oldRolesJson = JsonUtils.objectToJsonNotNulls(user.getRoles());

        user.setRoles(roles);
        userRepository.save(user);

        // Log the role change
        auditLogService.logAudit(user, "ROLE_CHANGE", "Role changed from " + oldRolesJson + " to " + JsonUtils.objectToJsonNotNulls(roles));
    }

    /**
     * Changes the language of the currently authenticated user.
     *
     * @param language the new language to be set
     */
    @Override
    public void changeLanguage(String language) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(localizationService.get("user.not.found")));
        user.setLanguage(language);
        userRepository.save(user);

    }
}
