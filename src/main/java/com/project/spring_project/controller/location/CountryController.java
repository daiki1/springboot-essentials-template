package com.project.spring_project.controller.location;

import com.project.spring_project.dto.locationDto.CountryDto;
import com.project.spring_project.dto.locationDto.StateDto;
import com.project.spring_project.service.location.CountryService;
import com.project.spring_project.service.location.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;
    private final StateService stateService;

    /**
     * Retrieves all countries from the repository.
     *
     * @return a list of all countries
     */
    @Operation(summary = "Get all countries", description = "Retrieves a list of all countries.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "countries retrieved successfully"),
            }
    )
    @GetMapping
    public List<CountryDto> getAllCountries( ) {
        return countryService.getAllCountries();
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

    /**
     * Retrieves all states of a country by its ID.
     *
     * @param countryId the ID of the country whose states to retrieve
     * @return a list of states belonging to the specified country
     */
    @Operation(summary = "Get all states of a country", description = "Retrieves a list of all states of a country.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "States retrieved successfully"),
            }
    )
    @GetMapping("/{countryId}/states")
    public ResponseEntity<List<StateDto>> getStatesByCountry(@PathVariable Long countryId) {
        List<StateDto> states = stateService.getStatesByCountry(countryId);
        return ResponseEntity.ok(states);
    }
}