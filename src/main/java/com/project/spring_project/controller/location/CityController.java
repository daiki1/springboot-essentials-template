package com.project.spring_project.controller.location;

import com.project.spring_project.dto.locationDto.CityDto;
import com.project.spring_project.service.location.CityService;
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
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    /**
     * Retrieves all cities from the repository.
     *
     * @return a list of all cities
     */
    @Operation(summary = "Get all cities", description = "Retrieves a paginated list of all cities.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cities retrieved successfully")
            }
    )
    @GetMapping
    public Page<CityDto> getAllCities(Pageable pageable) {
        return cityService.getAllCities(pageable);
    }

    /**
     * Retrieves a city by its ID.
     *
     * @param id the ID of the city to retrieve
     * @return an Optional containing the city if found, or empty if not found
     */
    @Operation(summary = "Get city by ID", description = "Retrieves a city by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "City retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "City not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CityDto> getCity(@PathVariable Long id) {
        return cityService.getCity(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}