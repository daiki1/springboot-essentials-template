package com.project.spring_project.security.filter;

import com.project.spring_project.BaseTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RateLimitingFilterTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rateLimiting_blocksAfterThreshold() throws Exception {
        String endpoint = "/api/auth/login"; // or any endpoint protected by rate limiting

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"user\", \"password\":\"Password123!\"}"))
                    .andExpect(status().isUnauthorized());
        }

        // 6th request should be rate-limited
        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\", \"password\":\"Password123!\"}"))
                .andExpect(status().isTooManyRequests());
    }
}