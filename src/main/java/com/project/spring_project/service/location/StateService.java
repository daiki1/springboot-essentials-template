package com.project.spring_project.service.location;

import com.project.spring_project.dto.locationDto.StateDto;

import java.util.List;
import java.util.Optional;

public interface StateService {
    List<StateDto> getStatesByCountry(Long countryId);
    Optional<StateDto> getState(Long id);
}
