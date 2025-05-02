package com.project.spring_project;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Profile("dev")
public class SampleTest {
    @Test
    void sampleTestMethod() {
        assertTrue(true);
    }

    @Test
    public void testExample() {
        assertEquals(2, 1 + 1);
    }
}
