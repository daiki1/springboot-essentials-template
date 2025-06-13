package com.project.spring_project.mapper;

import com.project.spring_project.dto.RoleDTO;
import com.project.spring_project.entity.user.Role;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    List<RoleDTO> toDTOs(List<Role> roles);
}