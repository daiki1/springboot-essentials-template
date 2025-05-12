package com.project.spring_project.controller.location;

import com.project.spring_project.dto.locationDto.CountryDto;
import com.project.spring_project.service.location.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    /**
     * Retrieves all countries from the repository.
     *
     * @return a list of all countries
     */
    @Operation(summary = "Get all countries", description = "Retrieves a paginated list of all countries.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "countries retrieved successfully"),
            }
    )
    @GetMapping
    public Page<CountryDto> getAllCountries(Pageable pageable) {
        return countryService.getAllCountries(pageable);
    }

    /**
     * Retrieves a country by its ID.
     *
     * @param id the ID of the country to retrieve
     * @return an Optional containing the country if found, or empty if not found
     */
    @Operation(summary = "Get country by ID", description = "Retrieves a country by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "country retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "country not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CountryDto> getCountry(@PathVariable Long id) {
        return countryService.getCountry(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}