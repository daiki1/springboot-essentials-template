package com.project.spring_project.service.location;

import com.project.spring_project.dto.locationDto.CityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CityService {
    Page<CityDto> getAllCities(Pageable pageable);
    Optional<CityDto> getCity(Long id);
}
