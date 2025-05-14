package com.project.spring_project.controller;


import com.project.spring_project.entity.location.Country;
import com.project.spring_project.entity.location.State;
import com.project.spring_project.repository.location.CountryRepository;
import com.project.spring_project.repository.location.StateRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CountryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;

    private Country testCountry;

    @BeforeAll
    void setup() {
        testCountry = countryRepository.save(new Country(999L, "Test Country", "", "", "", "", "", "", "", "", 0.0, 0.0, "", null));
        State state = stateRepository.save(new State(9999L, "Test State", 0.0, 0.0, testCountry, null));
    }

    @Test
    void testGetAllCountries() throws Exception {
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThan(0)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void testGetCountryById() throws Exception {
        mockMvc.perform(get("/api/countries/" + testCountry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Country"));
    }

    @Test
    void testGetCountryByInvalidId() throws Exception {
        mockMvc.perform(get("/api/countries/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStatesByCountry() throws Exception {
        mockMvc.perform(get("/api/countries/{countryId}/states", 999L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists());
    }

}