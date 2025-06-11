package com.project.spring_project.mapper;

import com.project.spring_project.dto.UserDto;
import com.project.spring_project.entity.user.Role;
import com.project.spring_project.entity.user.User;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper interface for converting between User and UserDto objects.
 * <p>
 * This interface uses MapStruct to generate the implementation for mapping between the User entity and UserDto.
 * It defines the mappings for the fields and provides custom mapping methods for roles.
 */
@Mapper(componentModel = "spring")
public interface  UserMapper {
    /**
     * Converts a User entity to a UserDto.
     *
     * @param user the User entity to convert
     * @return the converted UserDto
     */
    @Mappings({
            @Mapping(target = "userId", source = "id"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "active", source = "enabled"),
            @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))"),
            @Mapping(target = "locked", source = "accountLocked"),
            @Mapping(target = "lockedTime", source = "lockTime"),
            @Mapping(target = "language", source = "language"),
            @Mapping(target = "createdAt", source = "createdAt")
    })
    UserDto toDto(User user);

    /**
     * Converts a UserDto to a User entity.
     *
     * @param userDto the UserDto to convert
     * @return the converted User entity
     */
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

    /**
     * Converts the roles of a User entity to a set of role names.
     *
     * @param roles list of roles to convert
     * @return the converted set of role names
     */
    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }

    /**
     * Converts a set of role names to a set of Role entities.
     *
     * @param roleNames the set of role names to convert
     * @return the converted set of Role entities
     */
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
