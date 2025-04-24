package com.project.spring_project;

import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = "app.jwtSecret=yourTestSecretKey-long.enough.to.be.valid")
@SpringBootTest
public class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void testGenerateAndValidateToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(Set.of(role));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(user.getUsername());
        when(auth.getPrincipal()).thenReturn(user);

        String token = jwtTokenProvider.generateToken(auth);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    public void testGetUsernameFromToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(Set.of(role));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(user.getUsername());
        when(auth.getPrincipal()).thenReturn(user);

        String token = jwtTokenProvider.generateToken(auth);
        String username = jwtTokenProvider.getUsernameFromJWT(token);

        assertEquals("testuser", username);
    }


}