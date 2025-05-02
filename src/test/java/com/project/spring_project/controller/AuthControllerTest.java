package com.project.spring_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import com.project.spring_project.payload.request.AuthRequest;
import com.project.spring_project.payload.request.RegisterRequest;
import com.project.spring_project.utils.TestUserUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Profile("dev")
public class AuthControllerTest {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserUtil testUserUtil;

    @BeforeEach
    public void setupTestUser() {
        testUserUtil.registerUserIfNotExists();
    }

    @AfterEach
    void cleanTestUser() {
        testUserUtil.deleteTestUser();
    }

    private String getToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUserUtil.getRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        // Parse the response JSON and extract the token
        String responseContent = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseContent);

        return jsonNode.get("token").asText();
    }

    /*
    #############   REGISTER
    */

    @Test
    void testRegisterValidUser() {
        testUserUtil.deleteTestUser();// delete the test user to ensure a clean state
        testUserUtil.registerUserIfNotExists();

        Optional<User> userOpt = testUserUtil.fingUserByUsername(testUserUtil.getTestUsername());

        // Check if the user is present
        assertTrue(userOpt.isPresent());

        // Check if the username and email match
        assertEquals(testUserUtil.getEmail(), userOpt.get().getEmail());

        // Check if the password is hashed and not plain text
        assertFalse(userOpt.get().getPassword().contains(testUserUtil.getRawPassword()));
        assertTrue(userOpt.get().getPassword().startsWith("$2")); // BCrypt prefix

        // Check if the password matches the raw password
        assertTrue(testUserUtil.getPasswordService().matches(testUserUtil.getRawPassword(), userOpt.get().getPassword()));

        // Check if the user has the correct roles
        Set<String> roleNames = userOpt.get().getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        assertTrue(roleNames.contains("USER")); // or your default role

    }

    @Test
    void testRegisterWithExistingUsernameOrEmail() {
        // Attempt to register the same user again
        assertThrows(IllegalArgumentException.class, () -> testUserUtil.registerUserIfNotExists(true));
        testUserUtil.deleteTestUser();
    }

    @Test
    void testRegisterWithInvalidFields() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("");
        registerRequest.setPassword("abc");
        registerRequest.setEmail("invalida_email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    /*
    #############   LOGIN
    */

    @Test
    void jwtSecretShouldNotBeNull() {
        assertNotNull(jwtSecret);
    }

    @Test
    void testLoginWithRegisteredUser() throws Exception {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername(testUserUtil.getTestUsername());
        loginRequest.setPassword(testUserUtil.getRawPassword());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void loginShouldReturnToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUserUtil.getRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginWithInvalidCredentialsShouldFail() throws Exception {
        String invalidRequest = "{\"username\": \""+testUserUtil.getTestUsername()+"\", \"password\": \"Wrongpassword1!\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithMissingFields() throws Exception {
        String missingUsername = "{\"password\": \""+testUserUtil.getRawPassword()+"\"}";
        String missingPassword = "{\"username\": \""+testUserUtil.getTestUsername()+"\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingUsername))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingPassword))
                .andExpect(status().isBadRequest());

        String requestBody = "{}"; // empty JSON

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest()); // now expects 400
    }

    @Test
    void testLoginWithLockedUser_shouldFail() throws Exception {
        // Lock the user
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountLocked(true);
        testUserUtil.getUserRepository().save(user);

        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername(user.getUsername());
        loginRequest.setPassword(testUserUtil.getRawPassword());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isLocked()) // or .isForbidden() depending on implementation
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testLoginReturnsJwtWithCorrectClaims() throws Exception {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername(testUserUtil.getTestUsername());
        loginRequest.setPassword(testUserUtil.getRawPassword());

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = new ObjectMapper().readTree(responseJson).get("token").asText();

        // Decode JWT and assert claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8))) // Use the actual key
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(testUserUtil.getTestUsername(), claims.getSubject());
        assertTrue(claims.containsKey("roles")); // or check specific roles
    }

    /*
    #############   ACCESS
    */

    @Test
    public void shouldAccessPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAccessUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/user")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status()
                .isOk());
    }

    @Test
    public void shouldNotAccessUserEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldNotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isForbidden());
    }


    /*

    #Password Recovery
        Send recovery request for existing email.
        Handle non-existent email gracefully (no user enumeration).
        Token generation (hashed if applicable).
        Password reset success with valid token.
        Password reset failure: expired, malformed, or reused token.

       #Pepper and hashing
           Password hash varies with different peppers.
           Login fails if incorrect pepper is used.
           Pepper value is not stored in DB (can check DB directly).

       #Login
            Failure with locked account.
            JWT contains correct claims.
            Token expiration is correct.

        JWT tampering: test that altered tokens are rejected.


     */

}
