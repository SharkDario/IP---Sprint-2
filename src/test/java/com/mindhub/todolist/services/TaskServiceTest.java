package com.mindhub.todolist.services;


import com.mindhub.todolist.dtos.NewTask;
import com.mindhub.todolist.dtos.TaskDTO;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.repositories.EntityUserRepository;
import com.mindhub.todolist.repositories.TaskRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest {
    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private EntityUserRepository entityUserRepository;
    // Use @MockBean to replace real beans with mocks during testing, allowing you to focus on specific interactions.
    @Autowired
    private TaskService taskService;

    private Task testTask;

    private EntityUser testUser;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);


        // Create a test user
        testUser = Mockito.spy(new EntityUser("Miguel7", "12345678", "miguel@gmail.com"));
        when(testUser.getId()).thenReturn(1L);

        // Mock the repository to return the test user when findById is called
        when(entityUserRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(entityUserRepository.findByEmail("miguel@gmail.com")).thenReturn(Optional.of(testUser));
        when(entityUserRepository.findByUsername("Miguel7")).thenReturn(testUser);
        when(entityUserRepository.existsById(testUser.getId())).thenReturn(true);
        when(entityUserRepository.existsByEmail("miguel@gmail.com")).thenReturn(true);
        when(entityUserRepository.existsByUsername("Miguel7")).thenReturn(true);

        // Create a test task
        testTask = Mockito.spy(new Task("Test Title", "Test Description", TaskStatus.PENDING));
        when(testTask.getId()).thenReturn(1L);
        testTask.setUser(testUser);

        // Mock the repository to return the test task when findById is called
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));
        when(taskRepository.findByUserId(testUser.getId())).thenReturn(Collections.singletonList(testTask));
        when(taskRepository.existsById(testTask.getId())).thenReturn(true);
    }

    @Test
    public void testGetTaskDTOById() {
        // Mock the repository to return the test task
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        // Call the service method
        TaskDTO result = taskService.getTaskDTOById(testTask.getId());

        // Verify the result
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findById(testTask.getId());
    }

    @Test
    public void testGetTaskById() {
        // Mock the repository to return the test task
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        // Call the service method
        Task result = taskService.getTaskById(testTask.getId());

        // Verify the result
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findById(testTask.getId());
    }

    @Test
    public void testGetTaskById_NotFound() {
        // Mock the repository to return an empty optional
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Verify that the service throws an exception
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(999L));

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    public void testSaveTask() {
        // Mock the repository to return the test task when save is called
        when(taskRepository.save(testTask)).thenReturn(testTask);

        // Call the service method
        Task result = taskService.saveTask(testTask);

        // Verify the result
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        // Verify that the repository method was called
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    public void testCreateNewTask() {
        // Mock the repository to return the test user
        when(entityUserRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Mock the repository to return the test task when save is called
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Call the service method
        taskService.createNewTask(testUser.getId(), new NewTask("Test Title", "Test Description", TaskStatus.PENDING));

        // Verify that the repository methods were called
        verify(entityUserRepository, times(1)).findById(testUser.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testCreateNewTask_UserNotFound() {
        // Mock the repository to return an empty optional
        when(entityUserRepository.findById(999L)).thenReturn(Optional.empty());

        // Verify that the service throws an exception
        assertThrows(RuntimeException.class, () -> taskService.createNewTask(999L, new NewTask("Test Title", "Test Description", TaskStatus.PENDING)));

        // Verify that the repository method was called
        verify(entityUserRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetAllTasksById() {
        // Mock the repository to return a list containing the test task
        when(taskRepository.findByUserId(testUser.getId())).thenReturn(Collections.singletonList(testTask));

        // Call the service method
        List<TaskDTO> result = taskService.getAllTasksById(testUser.getId());

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
        assertEquals(testTask.getDescription(), result.get(0).getDescription());
        assertEquals(testTask.getStatus(), result.get(0).getStatus());

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findByUserId(testUser.getId());
    }

    @Test
    public void testGetAllTasks() {
        // Mock the repository to return a list containing the test task
        when(taskRepository.findAll()).thenReturn(Collections.singletonList(testTask));

        // Call the service method
        List<TaskDTO> result = taskService.getAllTasks();

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
        assertEquals(testTask.getDescription(), result.get(0).getDescription());
        assertEquals(testTask.getStatus(), result.get(0).getStatus());

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteTaskById() {
        // Mock the repository to return the test task
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        // Call the service method
        taskService.deleteTaskById(testTask.getId());

        // Verify that the repository method was called
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    public void testUpdateTask() {
        // Mock the repository to return the test task
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        Task newTask = new Task("Updated Title", "Updated Description", TaskStatus.COMPLETED);
        // Call the service method
        TaskDTO updatedTask = new TaskDTO(newTask);

        boolean result = taskService.updateTask(testTask.getId(), updatedTask);

        // Verify the result
        assertTrue(result);

        // Verify that the repository method was called
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    public void testUpdateTask_NotFound() {
        // Mock the repository to return an empty optional
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        Task newTask = new Task("Updated Title", "Updated Description", TaskStatus.COMPLETED);
        // Verify that the service throws an exception
        assertThrows(RuntimeException.class, () -> taskService.updateTask(999L, new TaskDTO(newTask)));

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    public void testDeleteTask() {
        // Mock the repository to return the test task
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        // Call the service method
        boolean result = taskService.deleteTask(testTask.getId());

        // Verify the result
        assertTrue(result);

        // Verify that the repository method was called
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    public void testIsTaskOwner() {
        // Mock the repository to return the test task
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        // Call the service method
        boolean result = taskService.isTaskOwner(testTask.getId(), "miguel@gmail.com");

        // Verify the result
        assertTrue(result);

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findById(testTask.getId());
    }

    @Test
    public void testIsTaskOwner_NotOwner() {
        // Mock the repository to return the test task
        when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        // Call the service method
        boolean result = taskService.isTaskOwner(testTask.getId(), "another@gmail.com");

        // Verify the result
        assertFalse(result);

        // Verify that the repository method was called
        verify(taskRepository, times(1)).findById(testTask.getId());
    }
}