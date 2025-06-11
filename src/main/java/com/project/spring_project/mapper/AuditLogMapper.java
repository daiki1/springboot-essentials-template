package com.project.spring_project.mapper;

import com.project.spring_project.dto.AuditLogDto;
import com.project.spring_project.entity.AuditLog;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mappings({
            @Mapping(target = "id", source = "audit.id"),
            @Mapping(target = "userId", source = "audit.user.id"),
            @Mapping(target = "username", source = "audit.user.username"),
            @Mapping(target = "operation", source = "audit.operation"),
            @Mapping(target = "timestamp", source = "audit.timestamp"),
            @Mapping(target = "details", source = "audit.details"),
            @Mapping(target = "resource", source = "audit.resource"),
            @Mapping(target = "ipAddress", source = "audit.ipAddress")
    })
    AuditLogDto toDto(AuditLog audit);

    @InheritInverseConfiguration(name = "toDto")
    @Mappings({
            @Mapping(target = "user", ignore = true)
    })
    AuditLog toEntity(AuditLogDto auditLogDto);
}
