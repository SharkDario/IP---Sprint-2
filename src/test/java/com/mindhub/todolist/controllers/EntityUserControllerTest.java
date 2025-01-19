package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dtos.*;
import com.mindhub.todolist.models.EntityUser;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// This annotation is used to test Spring MVC controllers,
// focusing only on the web layer
@WebMvcTest(EntityUserController.class)
public class EntityUserControllerTest {
    // Autowired to inject MockMvc for simulating HTTP requests
    @Autowired
    private MockMvc mockMvc;
    // MockBean to mock the EntityUserService dependency
    @MockBean
    private EntityUserService entityUserService;
    // MockBean to mock the JwtUtils dependency for JWT token handling
    @MockBean
    private JwtUtils jwtUtil;
    // Autowired to inject ObjectMapper for JSON serialization/deserialization
    @Autowired
    private ObjectMapper objectMapper;
    // Test user DTO object to be used in tests
    private EntityUserDTO testUser;
    // Constant for the test user's email
    private final String EMAIL = "dario@gmail.com";
    // JWT token for authentication
    private String token;
    // This method runs before each test to set up initial data
    @BeforeEach
    void setUp() {
        // Create a test user and its DTO
        EntityUser user = new EntityUser("Dario7", "12345678", EMAIL);
        testUser = new EntityUserDTO(user);
        // Generate a JWT token for the test user
        token = jwtUtil.generateToken(EMAIL);
    }
    // Test to verify that the /api/user/email endpoint returns the user's email
    @Test
    @WithMockUser(username = EMAIL) // Simulate a user with the given email
    void getEmailShouldReturnEmail() throws Exception {
        mockMvc.perform(get("/api/user/email"))
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string(EMAIL)); // Expect the response to contain the email
    }
    // Test to verify that the /api/user/profile endpoint returns the user's profile
    @Test
    @WithMockUser(username = EMAIL)
    void getProfileShouldReturnUserProfile() throws Exception {
        // Mock the service to return the test user DTO
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(testUser);

        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId())) // Verify the ID matches
                .andExpect(jsonPath("$.username").value(testUser.getUsername())) // Verify the username matches
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.role").value(testUser.getRole().toString()));
    }
    // Test to verify that the /api/user/profile endpoint returns a 404 when the user is not found
    @Test
    @WithMockUser(username = EMAIL)
    void getProfileShouldReturnNotFound() throws Exception {
        // Mock the service to throw an exception when the user is not found
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isNotFound()) // Expect HTTP 404 status
                .andExpect(content().string("User not found"));
    }
    // Test to verify that the /api/user/profile endpoint updates the user's profile
    @Test
    @WithMockUser(username = EMAIL)
    void updateProfileShouldUpdateUserProfile() throws Exception {
        // Create a DTO with updated username and email
        UpdateEntityUserUsernameEmailDTO updateDto = new UpdateEntityUserUsernameEmailDTO("newname", "newemail@example.com");
        // Mock the service to return the test user and confirm the update
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(testUser);
        when(entityUserService.updateEntityUserUsernameEmail(anyLong(), any(UpdateEntityUserUsernameEmailDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/user/profile")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .header("Authorization", "Bearer " + token) // Add JWT token for authentication
                        .contentType(MediaType.APPLICATION_JSON) // Set content type to JSON
                        .content(objectMapper.writeValueAsString(updateDto))) // Convert DTO to JSON
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string("User updated successfully")); // Expected message
    }
    // Test to verify that the /api/user/profile endpoint returns a 400 for invalid data
    @Test
    @WithMockUser(username = EMAIL)
    void updateProfileInvalidDataShouldReturnBadRequest() throws Exception {
        // Create a DTO with invalid data
        UpdateEntityUserUsernameEmailDTO updateDto = new UpdateEntityUserUsernameEmailDTO(" ", "invalid-email");
        // Mock the service to throw an exception for invalid data
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(testUser);
        when(entityUserService.updateEntityUserUsernameEmail(anyLong(), any(UpdateEntityUserUsernameEmailDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid data provided"));

        mockMvc.perform(put("/api/user/profile")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 status
                .andExpect(jsonPath("$.username").value("Username is required")) // Verify errors
                .andExpect(jsonPath("$.email").value("Email must be valid"));
    }
    // Test to verify that the /api/user/profile/password endpoint updates the user's password
    @Test
    @WithMockUser(username = EMAIL, password = "12345678", roles = "USER")
    void updatePasswordShouldUpdateUserPassword() throws Exception {
        // Create a DTO with the current and new password
        UpdateEntityUserPasswordDTO updateDto = new UpdateEntityUserPasswordDTO("12345678", "newpassword");
        // Mock the service to return the test user and confirm the password update
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(testUser);
        when(entityUserService.updateEntityUserPassword(eq(testUser.getId()), any(UpdateEntityUserPasswordDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/user/profile/password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string("Password updated successfully"));
    }
    // Test to verify that the /api/user/profile/password endpoint returns a 400 for an invalid current password
    @Test
    @WithMockUser(username = EMAIL, password = "12345678")
    void updatePasswordInvalidPasswordShouldReturnBadRequest() throws Exception {
        // Create a DTO with an incorrect current password
        UpdateEntityUserPasswordDTO updateDto = new UpdateEntityUserPasswordDTO("wrongpassword", "newpassword");
        // Mock the service to throw an exception for an invalid current password
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(testUser);
        when(entityUserService.updateEntityUserPassword(eq(testUser.getId()), any(UpdateEntityUserPasswordDTO.class)))
                .thenThrow(new IllegalArgumentException("Current password is incorrect"));

        mockMvc.perform(put("/api/user/profile/password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 status
                .andExpect(content().string("Invalid data provided: Current password is incorrect"));
    }
    // Test to verify that the /api/user/delete endpoint deletes the user
    @Test
    @WithMockUser(username = EMAIL)
    void deleteEntityUserShouldDeleteUser() throws Exception {
        // Mock the service to return the test user and confirm the deletion
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(testUser);
        when(entityUserService.deleteEntityUser(eq(testUser.getId()))).thenReturn(true);

        mockMvc.perform(delete("/api/user/delete")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }
    // Test to verify that the /api/user/delete endpoint returns a 404 when the user is not found
    @Test
    @WithMockUser(username = EMAIL)
    void deleteEntityUserUserNotFoundShouldReturnNotFound() throws Exception {
        // Mock the service to return the test user but fail to delete
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(testUser);
        when(entityUserService.deleteEntityUser(eq(testUser.getId()))).thenReturn(true);
        when(entityUserService.deleteEntityUser(eq(testUser.getId()))).thenReturn(false);

        mockMvc.perform(delete("/api/user/delete")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()) // Expect HTTP 404 status
                .andExpect(content().string("User not found"));
    }

}