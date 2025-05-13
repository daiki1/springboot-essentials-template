package com.project.spring_project.controller;

import com.project.spring_project.entity.location.Country;
import com.project.spring_project.entity.location.State;
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
public class StateControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private StateRepository stateRepository;
    @Autowired private CountryRepository countryRepository;

    private State testState;

    @BeforeAll
    void setup() {
        Country country = countryRepository.save(new Country(999L, "Test Country", "", "", "", "", "", "", "", "", 0.0, 0.0, "", null));
        testState = stateRepository.save(new State(9999L, "Test State", 0.0, 0.0, country, null));
    }

    @Test
    void testGetPaginatedStates() throws Exception {
        mockMvc.perform(get("/api/states")
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
    void testGetStateById() throws Exception {
        mockMvc.perform(get("/api/states/" + testState.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test State"));
    }

    @Test
    void testGetStateByInvalidId() throws Exception {
        mockMvc.perform(get("/api/states/999999"))
                .andExpect(status().isNotFound());
    }
}