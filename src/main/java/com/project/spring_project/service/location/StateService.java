package com.project.spring_project.service.location;

import com.project.spring_project.dto.locationDto.StateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StateService {
    Page<StateDto> getAllStates(Pageable pageable);
    Optional<StateDto> getState(Long id);
}
