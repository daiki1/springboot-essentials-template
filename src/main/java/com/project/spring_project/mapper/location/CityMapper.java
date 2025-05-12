package com.project.spring_project.mapper.location;

import com.project.spring_project.dto.locationDto.CityDto;
import com.project.spring_project.entity.location.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CityMapper {

    /**
     * Converts a City entity to a CityDto.
     *
     * @param city the City entity to convert
     * @return the converted CityDto
     */
    @Mappings({
            @Mapping(target = "stateId", source = "state.id"),
            @Mapping(target = "stateName", source = "state.name"),
    })
    CityDto toDto(City city);

}