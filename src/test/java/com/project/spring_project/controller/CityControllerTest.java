package com.project.spring_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring_project.entity.location.City;
import com.project.spring_project.entity.location.Country;
import com.project.spring_project.entity.location.State;
import com.project.spring_project.repository.location.CityRepository;
import com.project.spring_project.repository.location.CountryRepository;
import com.project.spring_project.repository.location.StateRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CityControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private CityRepository cityRepository;
    @Autowired private StateRepository stateRepository;
    @Autowired private CountryRepository countryRepository;

    private City testCity;

    @BeforeAll
    void setup() {
        Country country = countryRepository.save(new Country(999L, "Test Country", "", "", "", "", "", "", "", "", 0.0, 0.0, "", null));
        State state = stateRepository.save(new State(9999L, "Test State", 0.0, 0.0, country, null));
        testCity = cityRepository.save(new City(999999L, "Test City", 10.0, 20.0, state));
    }



    @Test
    void testGetCityById() throws Exception {
        mockMvc.perform(get("/api/cities/" + testCity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test City"));
    }

    @Test
    void testGetCityByInvalidId() throws Exception {
        mockMvc.perform(get("/api/cities/999991"))
                .andExpect(status().isNotFound());
    }
}
