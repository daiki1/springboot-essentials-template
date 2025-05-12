package com.project.spring_project.service.location;

import com.project.spring_project.dto.locationDto.CountryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CountryService {
    Page<CountryDto> getAllCountries(Pageable pageable);
    Optional<CountryDto> getCountry(Long id);
}
