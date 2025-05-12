package com.project.spring_project.service.impl.location;

import com.project.spring_project.dto.locationDto.CityDto;
import com.project.spring_project.mapper.location.CityMapper;
import com.project.spring_project.repository.location.CityRepository;
import com.project.spring_project.service.location.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    /**
     * Retrieves all cities from the repository.
     *
     * @return a list of all cities
     */
    @Override
    public Page<CityDto> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable)
                .map(cityMapper::toDto);
    }

    /**
     * Retrieves a city by its ID.
     *
     * @param id the ID of the city to retrieve
     * @return an Optional containing the city if found, or empty if not found
     */
    @Override
    public Optional<CityDto> getCity(Long id) {
        return cityRepository.findById(id)
                .map(cityMapper::toDto);
    }
}
