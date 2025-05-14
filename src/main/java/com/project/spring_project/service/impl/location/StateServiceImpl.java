package com.project.spring_project.service.impl.location;

import com.project.spring_project.dto.locationDto.StateDto;
import com.project.spring_project.mapper.location.StateMapper;
import com.project.spring_project.repository.location.StateRepository;
import com.project.spring_project.service.location.StateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<StateDto> getStatesByCountry(Long countryId) {
        return stateRepository.findByCountryId(countryId)
                .stream()
                .map(stateMapper::toDto)
                .collect(Collectors.toList());
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
