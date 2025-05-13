package com.project.spring_project.controller;

import com.project.spring_project.entity.location.Country;
import com.project.spring_project.repository.location.CountryRepository;
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
public class CountryControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeAll
    void setup() {
        testCountry = countryRepository.save(new Country(999L, "Test Country", "", "", "", "", "", "", "", "", 0.0, 0.0, "", null));
    }

    @Test
    void testGetPaginatedCountries() throws Exception {
        mockMvc.perform(get("/api/countries")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(lessThanOrEqualTo(10)))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").isNumber());
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
}