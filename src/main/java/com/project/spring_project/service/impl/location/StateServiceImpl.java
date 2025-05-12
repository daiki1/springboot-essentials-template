package com.project.spring_project.service.impl.location;

import com.project.spring_project.dto.locationDto.StateDto;
import com.project.spring_project.mapper.location.StateMapper;
import com.project.spring_project.repository.location.StateRepository;
import com.project.spring_project.service.location.StateService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class StateServiceImpl implements StateService {
    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    /**
     * Retrieves all states from the repository.
     *
     * @return a list of all states
     */
    @Override
    public Page<StateDto> getAllStates(Pageable pageable) {
        return stateRepository.findAll(pageable)
                .map(stateMapper::toDto);
    }

    /**
     * Retrieves a state by its ID.
     *
     * @param id the ID of the state to retrieve
     * @return an Optional containing the state if found, or empty if not found
     */
    @Override
    public Optional<StateDto> getState(Long id) {
        return stateRepository.findById(id)
                .map(stateMapper::toDto);
    }
}
