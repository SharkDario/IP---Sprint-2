package com.mindhub.todolist.repositories;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// This annotation is used for JPA tests, it configures an in-memory database and JPA repositories
@DataJpaTest
public class EntityUserRepositoryTest {
    // Autowired to inject the EntityUserRepository instance for testing
    @Autowired
    private EntityUserRepository userRepository;
    // EntityUser object to be used in tests
    private EntityUser user;
    // This method runs before each test to set up initial data
    @BeforeEach
    public void setUp() {
        // Create a new EntityUser and set its properties
        user = new EntityUser();
        user.setUsername("Dario7");
        user.setPassword("12345678");
        user.setEmail("dario@gmail.com");
        user.setRole(RoleType.USER);
        // Save the user to the in-memory database
        userRepository.save(user);
    }
    // Test to find a user by their username
    @Test
    public void testFindByUsername() {
        // Find the user by username
        EntityUser foundUser = userRepository.findByUsername("Dario7");
        // Assert that the user is found and the username matches
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("Dario7");
    }
    // Test to check if a user exists by their username
    @Test
    public void testExistsByUsername() {
        // Check if the user exists by username
        boolean exists = userRepository.existsByUsername("Dario7");
        // Assert that the user exists
        assertTrue(exists);
    }
    // Test to find a user by a non-existent username
    @Test
    public void testFindByUsernameNotFound() {
        // Try to find a user with a non-existent username
        EntityUser foundUser = userRepository.findByUsername("nonexistentUser");
        // Assert that no user is found
        assertThat(foundUser).isNull();
    }
    // Test to find a user by their email
    @Test
    public void testFindByEmail() {
        // Find the user by email
        Optional<EntityUser> foundUser = userRepository.findByEmail("dario@gmail.com");
        // Assert that the user is found and the email matches
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("dario@gmail.com");
    }
    // Test to find a user by their username and password
    @Test
    public void testFindByUsernameAndPassword() {
        // Find the user by username and password
        EntityUser foundUser = userRepository.findByUsernameAndPassword("Dario7", "12345678");
        // Assert that the user is found and the username and password match
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("Dario7");
        assertThat(foundUser.getPassword()).isEqualTo("12345678");
    }
    // Test to check if a user exists by their ID
    @Test
    public void testExistsById() {
        // Check if the user exists by ID
        boolean exists = userRepository.existsById(user.getId());
        // Assert that the user exists
        assertTrue(exists);
    }
    // Test to check if a user exists by their email
    @Test
    public void testExistsByEmail() {
        // Check if the user exists by email
        boolean exists = userRepository.existsByEmail("dario@gmail.com");
        // Assert that the user exists
        assertTrue(exists);
    }
    // Test to check if a user exists by their username and password
    @Test
    public void testExistsByUsernameAndPassword() {
        // Check if the user exists by username and password
        boolean exists = userRepository.existsByUsernameAndPassword("Dario7", "12345678");
        // Assert that the user exists
        assertTrue(exists);
    }
    // Test to check if a user exists by their email and a different ID
    @Test
    public void testExistsByEmailAndIdNot() {
        // Check if the user exists by email and a different ID
        boolean exists = userRepository.existsByEmailAndIdNot("dario@gmail.com", user.getId() + 1);
        // Assert that the user exists
        assertTrue(exists);
    }
    // Test to check if a user exists by their username and a different ID
    @Test
    public void testExistsByUsernameAndIdNot() {
        // Check if the user exists by username and a different ID
        boolean exists = userRepository.existsByUsernameAndIdNot("Dario7", user.getId() + 1);
        // Assert that the user exists
        assertTrue(exists);
    }
    // Test to count the number of users by their ID
    @Test
    public void testCountById() {
        // Count the number of users by ID
        int count = userRepository.countById(user.getId());
        // Assert that the count is 1
        assertThat(count).isEqualTo(1);
    }
    // Test to count the number of users by their username
    @Test
    public void testCountByUsername() {
        // Count the number of users by username
        int count = userRepository.countByUsername("Dario7");
        // Assert that the count is 1
        assertThat(count).isEqualTo(1);
    }
    // Test to count the number of users by their email
    @Test
    public void testCountByEmail() {
        // Count the number of users by email
        int count = userRepository.countByEmail("dario@gmail.com");
        // Assert that the count is 1
        assertThat(count).isEqualTo(1);
    }
    // Test to count the number of users by their username and password
    @Test
    public void testCountByUsernameAndPassword() {
        // Count the number of users by username and password
        int count = userRepository.countByUsernameAndPassword("Dario7", "12345678");
        // Assert that the count is 1
        assertThat(count).isEqualTo(1);
    }
    // Test to find a user by a non-existent email
    @Test
    public void testFindByEmailNotFound() {
        // Try to find a user with a non-existent email
        Optional<EntityUser> foundUser = userRepository.findByEmail("nonexistent@example.com");
        // Assert that no user is found
        assertThat(foundUser).isEmpty();
    }
    // Test to check if a user exists by a non-existent username
    @Test
    public void testExistsByUsernameNotFound() {
        // Check if a user exists by a non-existent username
        boolean exists = userRepository.existsByUsername("nonexistent");
        // Assert that the user does not exist
        assertFalse(exists);
    }
    // Test to check if a user exists by a non-existent email
    @Test
    public void testExistsByEmailNotFound() {
        // Check if a user exists by a non-existent email
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        // Assert that the user does not exist
        assertFalse(exists);
    }
    // Test to count the number of users by a non-existent ID
    @Test
    public void testCountByIdNotFound() {
        // Count the number of users by a non-existent ID
        int count = userRepository.countById(user.getId() + 1000);
        // Assert that the count is 0
        assertThat(count).isEqualTo(0);
    }
}