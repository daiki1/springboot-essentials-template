package com.project.spring_project;

import com.project.spring_project.secutrity.services.RateLimitingService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseTest {

    @Autowired
    protected RateLimitingService rateLimitingService;

    @BeforeEach
    void resetRateLimit() {
        rateLimitingService.resetAll();
    }
}