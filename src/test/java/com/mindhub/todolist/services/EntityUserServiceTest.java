package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.dtos.UpdateEntityUserUsernameEmailDTO;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.repositories.EntityUserRepository;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Use @SpringBootTest to load the full Spring context and verify component integration
@SpringBootTest
@ActiveProfiles("test")
public class EntityUserServiceTest {
    // Allows for creating and managing mocks of dependencies in unit tests,
    // facilitating the simulation of external components.
    // Simulacrum
    @MockBean
    private EntityUserRepository entityUserRepository;
    // Use @MockBean to replace real beans with mocks during testing, allowing you to focus on specific interactions.
    @Autowired
    private EntityUserService entityUserService;

    private EntityUser testUser;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        //testUser = new EntityUser("Miguel7", encodedPassword, "miguel@gmail.com");
        testUser = spy(new EntityUser("Miguel7", "12345678", "miguel@gmail.com"));
        when(testUser.getId()).thenReturn(1L);
        // Mock the repository to return the test user when findByEmail is called
        when(entityUserRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(entityUserRepository.findByEmail("miguel@gmail.com")).thenReturn(Optional.of(testUser));
        when(entityUserRepository.findByUsername("Miguel7")).thenReturn(testUser);
        when(entityUserRepository.existsById(testUser.getId())).thenReturn(true);
        when(entityUserRepository.existsByEmail("miguel@gmail.com")).thenReturn(true);
        when(entityUserRepository.existsByUsername("Miguel7")).thenReturn(true);
        //entityUserRepository = mock(EntityUserRepository.class);
    }

    @Test
    public void testGetEntityUserById() {
        // Mock the repository to return the test user when findById is called
        when(entityUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Call the service method
        EntityUser result = entityUserService.getEntityUserById(1L);

        // Verify the result
        assertNotNull(result);
        assertEquals("Miguel7", result.getUsername());
        assertEquals("miguel@gmail.com", result.getEmail());

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).findById(eq(1L));
    }

    @Test
    public void testGetEntityUserByIdNotFound() {
        // Mock the repository to return an empty optional when findById is called
        when(entityUserRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify that the service throws an exception when the user is not found
        assertThrows(RuntimeException.class, () -> entityUserService.getEntityUserById(1L));

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetEntityUserByEmail() {
        // Call the service method
        EntityUser result = entityUserService.getEntityUserByEmail("miguel@gmail.com");

        // Verify the result
        assertNotNull(result);
        assertEquals("Miguel7", result.getUsername());
        assertEquals("miguel@gmail.com", result.getEmail());

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).findByEmail("miguel@gmail.com");
    }

    @Test
    public void testGetEntityUserByEmailNotFound() {
        // Mock the repository to return an empty optional when findByEmail is called
        when(entityUserRepository.findByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());

        // Verify that the service throws an exception when the user is not found
        assertThrows(RuntimeException.class, () -> entityUserService.getEntityUserByEmail("nonexistent@gmail.com"));

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).findByEmail("nonexistent@gmail.com");
    }

    @Test
    public void testSaveEntityUser() {
        // Mock the repository to return the test user when save is called
        when(entityUserRepository.save(any(EntityUser.class))).thenReturn(testUser);

        // Call the service method
        EntityUser result = entityUserService.saveEntityUser(testUser);

        // Verify the result
        assertNotNull(result);
        assertEquals("Miguel7", result.getUsername());
        assertEquals("miguel@gmail.com", result.getEmail());

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).save(testUser);
    }

    @Test
    public void testRegisterAdminUser() {
        // Mock the repository to return false when checking for existing email and username
        when(entityUserRepository.existsByEmail("admin@gmail.com")).thenReturn(false);
        when(entityUserRepository.existsByUsername("AdminUser")).thenReturn(false);

        // Mock the repository to return the test user when save is called
        when(entityUserRepository.save(any(EntityUser.class))).thenReturn(testUser);

        // Call the service method
        NewEntityUser newUser = new NewEntityUser("AdminUser", "password", "admin@gmail.com");
        entityUserService.registerAdminUser(newUser);

        // Verify that the repository methods were called
        verify(entityUserRepository, times(1)).existsByEmail("admin@gmail.com");
        verify(entityUserRepository, times(1)).existsByUsername("AdminUser");
        verify(entityUserRepository, times(1)).save(any(EntityUser.class));
    }

    @Test
    public void testRegisterUser() {
        // Mock the repository to return false when checking for existing email and username
        when(entityUserRepository.existsByEmail("user@gmail.com")).thenReturn(false);
        when(entityUserRepository.existsByUsername("User")).thenReturn(false);

        // Mock the repository to return the test user when save is called
        when(entityUserRepository.save(any(EntityUser.class))).thenReturn(testUser);

        // Call the service method
        NewEntityUser newUser = new NewEntityUser("User", "password", "user@gmail.com");
        entityUserService.registerUser(newUser);

        // Verify that the repository methods were called
        verify(entityUserRepository, times(1)).existsByEmail("user@gmail.com");
        verify(entityUserRepository, times(1)).existsByUsername("User");
        verify(entityUserRepository, times(1)).save(any(EntityUser.class));
    }

    @Test
    public void testValidateEntityUserEmailAlreadyInUse() {
        // Mock the repository to return true when checking for existing email
        when(entityUserRepository.existsByEmail("existing@gmail.com")).thenReturn(true);

        // Verify that the service throws an exception when the email is already in use
        NewEntityUser newUser = new NewEntityUser("User", "password", "existing@gmail.com");
        assertThrows(IllegalArgumentException.class, () -> entityUserService.validateEntityUser(newUser));

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).existsByEmail("existing@gmail.com");
    }

    @Test
    public void testValidateEntityUserUsernameAlreadyInUse() {
        // Mock the repository to return true when checking for existing username
        when(entityUserRepository.existsByUsername("ExistingUser")).thenReturn(true);

        // Verify that the service throws an exception when the username is already in use
        NewEntityUser newUser = new NewEntityUser("ExistingUser", "password", "user@gmail.com");
        assertThrows(IllegalArgumentException.class, () -> entityUserService.validateEntityUser(newUser));

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).existsByUsername("ExistingUser");
    }

    @Test
    public void testUpdateEntityUserUsernameEmail() {
        // Mock the repository to return the test user when findById is called
        when(entityUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Mock the repository to return false when checking for existing email and username
        when(entityUserRepository.existsByEmailAndIdNot("newemail@gmail.com", 1L)).thenReturn(false);
        when(entityUserRepository.existsByUsernameAndIdNot("NewUsername", 1L)).thenReturn(false);

        // Call the service method
        UpdateEntityUserUsernameEmailDTO updatedUser = new UpdateEntityUserUsernameEmailDTO("NewUsername", "newemail@gmail.com");
        boolean result = entityUserService.updateEntityUserUsernameEmail(1L, updatedUser);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(entityUserRepository, times(1)).findById(1L);
        verify(entityUserRepository, times(1)).existsByEmailAndIdNot("newemail@gmail.com", 1L);
        verify(entityUserRepository, times(1)).existsByUsernameAndIdNot("NewUsername", 1L);
        verify(entityUserRepository, times(1)).save(testUser);
    }

    @Test
    public void testDeleteEntityUser() {
        // Mock the repository to return true when checking for existing user
        when(entityUserRepository.existsById(1l)).thenReturn(true);

        assertTrue(entityUserRepository.existsById(1L));
        // Call the service method
        boolean result = entityUserService.deleteEntityUser(1L);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(entityUserRepository, times(1)).existsById(1L);
        verify(entityUserRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteEntityUserNotFound() {
        // Mock the repository to return false when checking for existing user
        when(entityUserRepository.existsById(eq(2L))).thenReturn(false);

        // Call the service method
        boolean result = entityUserService.deleteEntityUser(eq(2L));

        // Verify the result
        assertFalse(result);
    }

    @Test
    public void testNullPasswordCreateUser() {
        // Create a new user with a null password
        NewEntityUser newUser = new NewEntityUser("username", null, "dario@gmail.com");

        // Manually validate the object
        Set<ConstraintViolation<NewEntityUser>> violations = validator.validate(newUser);

        // Verify that there is at least one violation
        assertFalse(violations.isEmpty());

        // Verify the violation message
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password is required")));
    }

    @Test
    public void testInvalidPasswordCreateUser() {
        // Create a new user with a password that is too short
        NewEntityUser newUser = new NewEntityUser("username", "123", "dario@gmail.com");

        // Manually validate the object
        Set<ConstraintViolation<NewEntityUser>> violations = validator.validate(newUser);

        // Verify that there is at least one violation
        assertFalse(violations.isEmpty());

        // Verify the violation message
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password must have at least 8 characters")));
    }

    @Test
    public void testInvalidUsernameCreateUser() {
        // Create a new user with a username that is too short
        NewEntityUser newUser = new NewEntityUser("usr", "12345678", "dario@gmail.com");

        // Manually validate the object
        Set<ConstraintViolation<NewEntityUser>> violations = validator.validate(newUser);

        // Verify that there is at least one violation
        assertFalse(violations.isEmpty());

        // Verify the violation message
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Username must be between 4 and 10 characters")));
    }

    @Test
    public void testInvalidEmailCreateUser() {
        // Create a new user with an invalid email
        NewEntityUser newUser = new NewEntityUser("username", "12345678", "invalid-email");

        // Manually validate the object
        Set<ConstraintViolation<NewEntityUser>> violations = validator.validate(newUser);

        // Verify that there is at least one violation
        assertFalse(violations.isEmpty());

        // Verify the violation message
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email must be valid")));
    }
}