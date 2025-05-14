package com.project.spring_project.service.location;

import com.project.spring_project.dto.locationDto.CityDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CityService {
    Page<CityDto> getCitiesByState(Long stateId, int page, int size);
    Optional<CityDto> getCity(Long id);
}
