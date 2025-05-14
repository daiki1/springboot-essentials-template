package com.project.spring_project.service.impl.location;

import com.project.spring_project.dto.locationDto.CountryDto;
import com.project.spring_project.mapper.location.CountryMapper;
import com.project.spring_project.repository.location.CountryRepository;
import com.project.spring_project.service.location.CountryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    /**
     * Retrieves all countries from the repository.
     *
     * @return a list of all countries
     */
    @Override
    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll()
                .stream()
                .map(countryMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a country by its ID.
     *
     * @param id the ID of the country to retrieve
     * @return an Optional containing the country if found, or empty if not found
     */
    @Override
    public Optional<CountryDto> getCountry(Long id) {
        return countryRepository.findById(id)
                .map(countryMapper::toDto);
    }
}
