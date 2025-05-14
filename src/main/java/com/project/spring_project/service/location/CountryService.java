package com.project.spring_project.service.location;

import com.project.spring_project.dto.locationDto.CountryDto;

import java.util.List;
import java.util.Optional;

public interface CountryService {
    List<CountryDto> getAllCountries();
    Optional<CountryDto> getCountry(Long id);
}
