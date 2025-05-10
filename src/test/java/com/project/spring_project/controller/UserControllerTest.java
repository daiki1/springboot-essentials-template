package com.project.spring_project.controller;

import com.project.spring_project.dto.request.AuthRequest;
import com.project.spring_project.dto.response.AuthResponse;
import com.project.spring_project.entity.Role;
import com.project.spring_project.entity.User;
import com.project.spring_project.service.AuthService;
import com.project.spring_project.utils.TestUserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Profile("dev")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private TestUserUtil testUserUtil;

    private User currentUser;

    @BeforeEach
    public void setupTestUser() {
        testUserUtil.registerUserIfNotExists();
        //Add admin role to be able to access the endpoints
        User user = testUserUtil.getUserRepository().findByUsername(testUserUtil.getTestUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = testUserUtil.getRoleRepository().findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));
        testUserUtil.getUserRepository().save(user);

        currentUser = user;
    }

    @AfterEach
    void cleanTestUser() {
        testUserUtil.deleteTestUser();
    }

    private String getToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(testUserUtil.getTestUsername());
        request.setPassword(testUserUtil.getRawPassword());
        AuthResponse authResponse = testUserUtil.getAuthService().login(request);

        return authResponse.getToken();
    }

    @Test
    void listUsers_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/" + currentUser.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByName_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/username/"+testUserUtil.getTestUsername())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByEmail_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/email/"+testUserUtil.getEmail())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_shouldUpdateAndReturnOk() throws Exception {
        String body = "{\"email\": \"updatedMail@mail.com\"}";

        mockMvc.perform(put("/api/users/"+currentUser.getId())
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/users/"+currentUser.getId())
                        .header("Authorization", "Bearer " + getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_shouldReturnOk() throws Exception {
        String body = "{\"active\": true}";

        mockMvc.perform(patch("/api/users/"+currentUser.getId()+"/status")
                        .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void changeRoles_shouldReturnOk() throws Exception {
        String body = "{\"roles\": [\"USER\"]}";

        mockMvc.perform(put("/api/users/"+currentUser.getId()+"/roles")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void changeLanguage_shouldReturnOk() throws Exception {
        String body = "{\"language\": \"es\"}";

        mockMvc.perform(put("/api/users/language")
                        .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

}
