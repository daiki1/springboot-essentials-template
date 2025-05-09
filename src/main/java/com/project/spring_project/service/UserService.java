package com.project.spring_project.service;

import com.project.spring_project.dto.UserDto;
import com.project.spring_project.dto.request.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserDto> getAllUsers(Pageable pageable);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    UserDto getUserByUsername(String username);
    UserDto updateUser(Long id, UserUpdateRequest request);
    void deactivateUser(Long id);
    void updateUserStatus(Long id, boolean active);
    void updateUserRoles(Long userId, List<String> newRoles);
    void changeLanguage(String language);
}
