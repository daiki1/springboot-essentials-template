package com.project.spring_project;

import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.Date;
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

    @Test
    void testTokenExpiration() {
        String token = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // already expired
                .signWith(jwtTokenProvider.getKey(), SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testInvalidSignature() {
        String token = Jwts.builder()
                .setSubject("user")
                .signWith(Keys.hmacShaKeyFor("WrongSecretKeyForTesting1234567890".getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testNullOrBlankToken() {
        assertFalse(jwtTokenProvider.validateToken(null));
        assertFalse(jwtTokenProvider.validateToken(""));
    }

}