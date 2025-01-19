package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dtos.*;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.services.EntityUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityUserService entityUserService;

    @MockBean
    private JwtUtils jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private EntityUserDTO testAdmin;

    private final String EMAIL = "dario@gmail.com";
    private String token;

    @BeforeEach
    void setUp() {
        EntityUser user = new EntityUser("Dario7", "12345678", EMAIL);
        user.setRole(RoleType.ADMIN);
        testAdmin = new EntityUserDTO(user);
        token = jwtUtil.generateToken(EMAIL);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void getAllUsersShouldReturnUsers() throws Exception {
        // Mock the service to return a list of users
        List<EntityUserDTO> users = Collections.singletonList(testAdmin);
        when(entityUserService.getAllEntityUsers()).thenReturn(users);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/admin/users")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testAdmin.getId()))
                .andExpect(jsonPath("$[0].username").value(testAdmin.getUsername()))
                .andExpect(jsonPath("$[0].email").value(testAdmin.getEmail()))
                .andExpect(jsonPath("$[0].role").value(testAdmin.getRole().toString()));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void getUserByIdShouldReturnUser() throws Exception {
        // Mock the service to return a user
        when(entityUserService.getEntityUserDTOById(1L)).thenReturn(testAdmin);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/admin/user/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAdmin.getId()))
                .andExpect(jsonPath("$.username").value(testAdmin.getUsername()))
                .andExpect(jsonPath("$.email").value(testAdmin.getEmail()))
                .andExpect(jsonPath("$.role").value(testAdmin.getRole().toString()));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void getUserByIdShouldReturnNotFound() throws Exception {
        // Mock the service to throw an exception
        when(entityUserService.getEntityUserDTOById(1L)).thenThrow(new RuntimeException("User not found"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/admin/user/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void createEntityUserShouldCreateUser() throws Exception {
        // Mock the service to create a user
        NewEntityUser newUser = new NewEntityUser("Dario7", "12345678", EMAIL);
        entityUserService.registerUser(any(NewEntityUser.class));

        // Perform the request and verify the response
        mockMvc.perform(post("/api/admin/user")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User created successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void createAdminShouldCreateAdmin() throws Exception {
        // Mock the service to create an admin
        NewEntityUser newAdmin = new NewEntityUser("Dario7", "12345678", EMAIL);
        entityUserService.registerAdminUser(any(NewEntityUser.class));

        // Perform the request and verify the response
        mockMvc.perform(post("/api/admin/admin")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdmin)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Admin created successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void updateEntityUserShouldUpdateUser() throws Exception {
        // Mock the service to update a user
        UpdateEntityUserUsernameEmailDTO updatedUser = new UpdateEntityUserUsernameEmailDTO("NewUser", "newemail@example.com");
        when(entityUserService.updateEntityUserUsernameEmail(eq(1L), any(UpdateEntityUserUsernameEmailDTO.class))).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/admin/user/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void updateEntityUserShouldReturnBadRequest() throws Exception {
        // Mock the service to throw an exception
        UpdateEntityUserUsernameEmailDTO updatedUser = new UpdateEntityUserUsernameEmailDTO(null, "invalid-email");
        when(entityUserService.updateEntityUserUsernameEmail(eq(1L), any(UpdateEntityUserUsernameEmailDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid data provided"));

        // Perform the request and verify the response
        mockMvc.perform(put("/api/admin/user/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username is required"))
                .andExpect(jsonPath("$.email").value("Email must be valid"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void updatePasswordShouldUpdatePassword() throws Exception {
        // Mock the service to update a password
        UpdateEntityUserPasswordDTO updatedPassword = new UpdateEntityUserPasswordDTO("oldPassword", "newPassword");
        when(entityUserService.updateEntityUserPassword(eq(1L), any(UpdateEntityUserPasswordDTO.class))).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/admin/user/{id}/password", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPassword)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void updatePasswordShouldReturnBadRequest() throws Exception {
        // Mock the service to throw an exception
        UpdateEntityUserPasswordDTO updatedPassword = new UpdateEntityUserPasswordDTO("wrongPassword", "newPassword");
        when(entityUserService.updateEntityUserPassword(eq(1L), any(UpdateEntityUserPasswordDTO.class)))
                .thenThrow(new IllegalArgumentException("Current password is incorrect"));

        // Perform the request and verify the response
        mockMvc.perform(put("/api/admin/user/{id}/password", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPassword)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data provided: Current password is incorrect"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void deleteEntityUserShouldDeleteUser() throws Exception {
        // Mock the service to delete a user
        when(entityUserService.deleteEntityUser(1L)).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/admin/user/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void deleteEntityUserShouldReturnNotFound() throws Exception {
        // Mock the service to return false for deletion
        when(entityUserService.deleteEntityUser(1L)).thenReturn(false);

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/admin/user/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }
}