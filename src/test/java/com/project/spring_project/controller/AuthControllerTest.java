package com.project.spring_project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring_project.BaseTest;
import com.project.spring_project.entity.PasswordResetToken;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Profile("dev")
public class AuthControllerTest extends BaseTest {

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

    @Test
    void jwtTampering_alteredTokenIsRejected() throws Exception {
        // Login to get a valid token
        String validToken = getToken();

        // Tamper the token: e.g., change a character in the payload
        String[] parts = validToken.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts");

        // Alter the payload (2nd part) slightly
        String tamperedPayload = parts[1].substring(0, parts[1].length() - 1) + "X";
        String tamperedToken = parts[0] + "." + tamperedPayload + "." + parts[2];

        // Try to access a protected endpoint using the tampered token
        mockMvc.perform(get("/api/test/user") // replace with actual protected endpoint
                        .header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isForbidden()); // or .isForbidden() depending on your filter
    }

    /*
    #############   CHANGE ROLES
    */

    /*@Test
    void changingUserRoles() throws Exception {
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = testUserUtil.getRoleRepository().findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ROLE not found"));

        user.setRoles(Set.of(newRole));
        testUserUtil.getUserRepository().save(user);

        String request = "{\"username\": \""+testUserUtil.getTestUsername()+"\", \"roles\": [\"AUDIT\"] }";

        // Change roles
        mockMvc.perform(post("/api/auth/change_roles")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

    }*/

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
    #############   PASSWORD RECOVERY
    */

    @Test
    void sendRecoveryRequest_existingEmail_returnsOkAndSendsToken() throws Exception {
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Delete any existing token for the user however this is not necessary
        testUserUtil.deletePasswordResetToken(user.getId());

        mockMvc.perform(post("/api/auth/request-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + testUserUtil.getEmail() + "\"}"))
                .andExpect(status().isOk());

        assertFalse(testUserUtil.findRefreshTokensByUser(user.getId()).isEmpty() , "Token should be stored");
    }

    @Test
    void sendRecoveryRequest_nonExistentEmail_returnsOkButNoTokenLeak() throws Exception {
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Delete any existing token for the user however this is not necessary
        testUserUtil.deletePasswordResetToken(user.getId());

        mockMvc.perform(post("/api/auth/request-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\"}"))
                .andExpect(status().isBadRequest());

        assertTrue(testUserUtil.findRefreshTokensByUser(user.getId()).isEmpty(), "No token should be created for unknown email");
    }

    @Test
    void passwordReset_withValidToken_updatesPassword() throws Exception {
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Delete any existing token for the user however this is not necessary
        testUserUtil.deletePasswordResetToken(user.getId());

        mockMvc.perform(post("/api/auth/request-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \""+testUserUtil.getEmail()+"\"}"))
                .andExpect(status().isOk());

        Optional<PasswordResetToken> passwordResetToken = testUserUtil.findRefreshTokensByUser(user.getId());

        assertTrue(passwordResetToken.isPresent(), "Token should be created");

        String newPassword = "newSecurePassword1!";
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"" + passwordResetToken.get().getToken() + "\", \"newPassword\": \"" + newPassword + "\"}"))
                .andExpect(status().isOk());

        User updatedUser = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertTrue(testUserUtil.getPasswordService().matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    void passwordReset_withExpiredToken_fails() throws Exception {
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Delete any existing token for the user however this is not necessary
        testUserUtil.deletePasswordResetToken(user.getId());

        mockMvc.perform(post("/api/auth/request-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \""+testUserUtil.getEmail()+"\"}"))
                .andExpect(status().isOk());

        Optional<PasswordResetToken> passwordResetToken = testUserUtil.findRefreshTokensByUser(user.getId());

        assertTrue(passwordResetToken.isPresent(), "Token should be created");

        passwordResetToken.get().setExpiryDate(LocalDateTime.now().minusMinutes(1));
        testUserUtil.getPasswordResetTokenRepository().save(passwordResetToken.get());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"" + passwordResetToken.get().getToken() + "\", \"newPassword\": \"irrelevant1!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void passwordReset_withMalformedToken_fails() throws Exception {
        mockMvc.perform(post("/api/auth/request-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"invalid-token\", \"password\": \"newpass\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void passwordReset_withReusedToken_failsSecondTime() throws Exception {
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Delete any existing token for the user however this is not necessary
        testUserUtil.deletePasswordResetToken(user.getId());

        mockMvc.perform(post("/api/auth/request-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \""+testUserUtil.getEmail()+"\"}"))
                .andExpect(status().isOk());

        Optional<PasswordResetToken> passwordResetToken = testUserUtil.findRefreshTokensByUser(user.getId());

        assertTrue(passwordResetToken.isPresent(), "Token should be created");

        // First attempt - success
        String newPassword = "newSecurePassword1!";
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"" + passwordResetToken.get().getToken() + "\", \"newPassword\": \""+newPassword+"\"}"))
                .andExpect(status().isOk());

        // Second attempt - should fail
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"" + passwordResetToken.get().getToken() + "\", \"newPassword\": \"irrelevant1!\"}"))
                .andExpect(status().isBadRequest());
    }

}
