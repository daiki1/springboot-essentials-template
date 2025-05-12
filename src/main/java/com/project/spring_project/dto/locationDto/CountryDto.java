package com.project.spring_project.dto.locationDto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for State entity.
 * This class is used to transfer state data between layers.
 */
@Getter
@Setter
public class CountryDto {
    private Long id;
    private String name;
    private String iso3;
    private String iso2;
    private String phonecode;
    private String capital;
    private String currency;
    private String nativeName;
    private String region;
    private String subregion;
    private Double latitude;
    private Double longitude;
    private String emojiU;
}
