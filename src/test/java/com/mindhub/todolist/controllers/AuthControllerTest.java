package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dtos.LoginRequest;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.services.EntityUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// This annotation is used to test Spring MVC controllers, focusing only on the web layer
// Disables security filters to allow testing without authentication
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    // Autowired to inject MockMvc for simulating HTTP requests
    @Autowired
    private MockMvc mockMvc;
    // MockBean to mock the AuthenticationManager dependency for authentication
    @MockBean
    private AuthenticationManager authenticationManager;
    // MockBean to mock the EntityUserService dependency for user-related operations
    @MockBean
    private EntityUserService entityUserService;
    // MockBean to mock the JwtUtils dependency for JWT token handling
    @MockBean
    private JwtUtils jwtUtil;
    // Autowired to inject ObjectMapper for JSON serialization/deserialization
    @Autowired
    private ObjectMapper objectMapper;
    // Test objects for valid login and registration requests
    private LoginRequest validLoginRequest;
    private NewEntityUser validRegistrationRequest;
    // This method runs before each test to set up initial data
    @BeforeEach
    void setUp() {
        // Create a valid login request with email and password
        validLoginRequest = new LoginRequest("dario@gmail.com", "12345678");
        // Create a valid registration request with username, password, and email
        validRegistrationRequest = new NewEntityUser("testUser", "12345678", "dario@gmail.com");
    }
    // Test to verify that the /api/auth/login endpoint returns a JWT token for valid credentials
    @Test
    void authenticateUserWithValidCredentialsShouldReturnToken() throws Exception {
        // Mock the authentication process
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getName()).thenReturn("dario@gmail.com");
        // Mock the authenticated user's email
        when(jwtUtil.generateToken("dario@gmail.com")).thenReturn("valid.jwt.token");
        // Perform a POST request to the login endpoint with valid credentials
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON) // Set content type to JSON
                        .content(objectMapper.writeValueAsString(validLoginRequest))) // Convert login request to JSON
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string("valid.jwt.token")); // Expect the response to contain the JWT token
        // Verify that the authentication manager and JWT utility were called
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("dario@gmail.com");
    }
    // Test to verify that the /api/auth/login endpoint returns a 500 error for invalid credentials
    @Test
    void authenticateUserWithInvalidCredentialsShouldReturn500() throws Exception {
        // Mock the authentication process to throw an exception for invalid credentials
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected server error"));
        // Perform a POST request to the login endpoint with valid credentials
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isInternalServerError()); // Expect HTTP 500 status
        // Verify that the authentication manager was called, but JWT utility was not
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtil);
    }
    // Test to verify that the /api/auth/register endpoint successfully registers a user with valid data
    @Test
    void registerUserWithValidDataShouldReturnSuccess() throws Exception {
        // Mock the user registration process to do nothing (successful registration)
        doNothing().when(entityUserService).registerUser(any(NewEntityUser.class));
        // Perform a POST request to the register endpoint with valid registration data
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string("User registered successfully")); // Expect the success message
        // Verify that the user service was called to register the user
        verify(entityUserService).registerUser(any(NewEntityUser.class));
    }
}
