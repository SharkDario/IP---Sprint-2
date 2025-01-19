package com.mindhub.todolist.repositories;

import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// This annotation is used for JPA tests, it configures an in-memory database and JPA repositories
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EntityUserRepository userRepository;

    private Task task;
    private EntityUser user;

    @BeforeEach
    public void setUp(){
        // Create a user
        user = new EntityUser();
        user.setUsername("Dario7");
        user.setPassword("12345678");
        user.setEmail("dario@gmail.com");
        user.setRole(RoleType.USER);
        userRepository.save(user);
        // Create a task associated with the user
        task = new Task();
        task.setTitle("Sprint 4");
        task.setDescription("Testing");
        task.setStatus(TaskStatus.PENDING);
        task.setUser(user);
        taskRepository.save(task);
    }

    @Test
    public void testExistsById(){
        // Test if a task exists by its ID
        boolean exists = taskRepository.existsById(task.getId());
        assertTrue(exists);
    }

    @Test
    public void testFindById() {
        // Test finding a task by its ID
        Task foundTask = taskRepository.findById(task.getId()).orElse(null);
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getTitle()).isEqualTo("Sprint 4");
        assertThat(foundTask.getDescription()).isEqualTo("Testing");
        assertThat(foundTask.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    public void testFindByTitle() {
        // Test finding a task by its title
        Task foundTask = taskRepository.findByTitle("Sprint 4");
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(task.getId());
    }

    @Test
    public void testFindByDescription() {
        // Test finding a task by its description
        Task foundTask = taskRepository.findByDescription("Testing");
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(task.getId());
    }

    @Test
    public void testFindByUser() {
        // Test finding a task by its associated user
        Task foundTask = taskRepository.findByUser(user);
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(task.getId());
    }

    @Test
    public void testFindByUserId() {
        // Test finding tasks by user ID
        List<Task> foundTasks = taskRepository.findByUserId(user.getId());
        assertThat(foundTasks).isNotNull();
        assertThat(foundTasks.get(0).getId()).isEqualTo(task.getId());
    }

    @Test
    public void testExistsByTitle() {
        // Test if a task exists by its title
        boolean exists = taskRepository.existsByTitle("Sprint 4");
        assertTrue(exists);
    }

    @Test
    public void testExistsByDescription() {
        // Test if a task exists by its description
        boolean exists = taskRepository.existsByDescription("Testing");
        assertTrue(exists);
    }

    @Test
    public void testExistsByUser() {
        // Test if a task exists for a specific user
        boolean exists = taskRepository.existsByUser(user);
        assertTrue(exists);
    }

    @Test
    public void testCountById() {
        // Test counting tasks by ID
        int count = taskRepository.countById(task.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testCountByTitle() {
        // Test counting tasks by title
        int count = taskRepository.countByTitle("Sprint 4");
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testCountByDescription() {
        // Test counting tasks by description
        int count = taskRepository.countByDescription("Testing");
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testCountByUser() {
        // Test counting tasks by user
        int count = taskRepository.countByUser(user);
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testFindByIdNotFound() {
        // Test finding a non-existent task by ID
        Optional<Task> foundTask = taskRepository.findById(999L);
        assertThat(foundTask).isEmpty();
    }

    @Test
    public void testExistsByIdNotFound() {
        // Test checking existence of a non-existent task by ID
        boolean exists = taskRepository.existsById(999L);
        assertFalse(exists);
    }

    @Test
    public void testFindByTitleNotFound() {
        // Test finding a non-existent task by title
        Task foundTask = taskRepository.findByTitle("Non-existent Task");
        assertThat(foundTask).isNull();
    }
}