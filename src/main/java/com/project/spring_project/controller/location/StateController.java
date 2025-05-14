package com.project.spring_project.controller.location;

import com.project.spring_project.dto.locationDto.CityDto;
import com.project.spring_project.dto.locationDto.StateDto;
import com.project.spring_project.service.location.CityService;
import com.project.spring_project.service.location.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/states")
@RequiredArgsConstructor
public class StateController {
    private final StateService stateService;
    private final CityService cityService;

    /**
     * Retrieves a state by its ID.
     *
     * @param id the ID of the state to retrieve
     * @return an Optional containing the state if found, or empty if not found
     */
    @Operation(summary = "Get state by ID", description = "Retrieves a state by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "State retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "State not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<StateDto> getState(@PathVariable Long id) {
        return stateService.getState(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{stateId}/cities")
    public ResponseEntity<Page<CityDto>> getCitiesByState(
            @PathVariable Long stateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CityDto> cities = cityService.getCitiesByState(stateId, page, size);
        return ResponseEntity.ok(cities);
    }
}