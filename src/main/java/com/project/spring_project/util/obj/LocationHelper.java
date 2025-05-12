package com.project.spring_project.util.obj;

import lombok.Getter;
import lombok.Setter;
/**
 * Helper class to hold location information.
 * This class is used to transfer location data between layers.
 * Used only on state and city creation.
 */
@Getter
@Setter
public class LocationHelper {
    private Long id;
    private Long country_id;
    private Long state_id;
}
