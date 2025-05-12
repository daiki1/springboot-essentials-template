package com.project.spring_project.dto.locationDto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for State entity.
 * This class is used to transfer state data between layers.
 */
@Getter
@Setter
public class CityDto {
    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Long stateId;
    private String stateName;
}
