package com.project.spring_project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring_project.payload.request.AuthRequest;
import com.project.spring_project.utils.TestUserUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

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
        //testUserUtil.deleteTestUser();
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
    public void shouldAccessPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/user")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status()
                .isOk());
    }

    @Test
    public void shouldNotAccessAdminEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isForbidden());
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

    /*
    #Registration
        Register valid user.
        Register with existing username/email.
        Register with invalid fields (e.g., short password, blank fields).
        Confirm password encryption (BCrypt, pepper).
        The password is stored hashed.
        The correct roles are assigned.

    #Account Lock Handling
        Locked user cannot authenticate (simulate a locked user).
        Unlocked user can authenticate.

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
            Success with correct credentials.
            Failure with wrong password.
            Failure with locked account.
            JWT contains correct claims.
            Token expiration is correct.

        JWT tampering: test that altered tokens are rejected.


     */

}
