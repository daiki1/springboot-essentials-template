package com.project.spring_project.mapper;

import com.project.spring_project.dto.UserDto;
import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface  UserMapper {
    @Mappings({
            @Mapping(target = "userId", source = "id"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "active", source = "enabled"),
            @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))"),
            @Mapping(target = "locked", source = "accountLocked"),
            @Mapping(target = "lockedTime", source = "lockTime"),
            @Mapping(target = "language", source = "language"),
    })
    UserDto toDto(User user);

    @InheritInverseConfiguration(name = "toDto")
    @Mappings({
            @Mapping(target = "roles", expression = "java(mapRoleNamesToRoles(userDto.getRoles()))"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "activeToken", ignore = true),
            @Mapping(target = "failedAttempts", ignore = true),
            @Mapping(target = "password", ignore = true)
    })
    User toEntity(UserDto userDto);

    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
    default Set<Role> mapRoleNamesToRoles(Set<String> roleNames) {
        if (roleNames == null) return new HashSet<>();
        return roleNames.stream()
                .map(name -> {
                    Role role = new Role();
                    role.setName(name);
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
