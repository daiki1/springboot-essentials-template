package com.project.spring_project.mapper.location;

import com.project.spring_project.dto.locationDto.CountryDto;
import com.project.spring_project.entity.location.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    /**
     * Converts a Country entity to a CountryDto.
     *
     * @param country the Country entity to convert
     * @return the converted CountryDto
     */
    CountryDto toDto(Country country);
}