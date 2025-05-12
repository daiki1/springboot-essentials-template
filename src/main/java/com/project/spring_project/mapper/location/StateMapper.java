package com.project.spring_project.mapper.location;

import com.project.spring_project.dto.locationDto.StateDto;
import com.project.spring_project.entity.location.State;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StateMapper {

    /**
     * Converts a State entity to a StateDto.
     *
     * @param state the State entity to convert
     * @return the converted StateDto
     */
    @Mappings({
            @Mapping(target = "countryId", source = "country.id"),
            @Mapping(target = "countryName", source = "country.name")
    })
    StateDto toDto(State state);
}