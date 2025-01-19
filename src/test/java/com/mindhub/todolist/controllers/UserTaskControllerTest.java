package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.controllers.UserTaskController;
import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.TaskDTO;
import com.mindhub.todolist.dtos.NewTask;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.services.TaskService;
import com.mindhub.todolist.services.EntityUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserTaskController.class)
public class UserTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private EntityUserService entityUserService;

    @MockBean
    private JwtUtils jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private TaskDTO testTask;
    private String token;
    private final String EMAIL = "miguel@gmail.com";

    @BeforeEach
    void setUp() {
        // Create a test task
        Task task = Mockito.mock(Task.class);
        when(task.getId()).thenReturn(1L);
        when(task.getTitle()).thenReturn("Test Title");
        when(task.getDescription()).thenReturn("Test Description");
        when(task.getStatus()).thenReturn(TaskStatus.PENDING);

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("12345678")).thenReturn(encodedPassword);

        EntityUser user = Mockito.mock(EntityUser.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("Miguel7");
        when(user.getEmail()).thenReturn(EMAIL);

        when(task.getUser()).thenReturn(user);
        testTask = new TaskDTO(task);

        // Mock the authenticated user
        EntityUserDTO userDTO = new EntityUserDTO(user);
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(userDTO);

        token = jwtUtil.generateToken(EMAIL);
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getOwnTasksShouldReturnTasks() throws Exception {
        // Mock the service to return a list of tasks
        List<TaskDTO> tasks = Collections.singletonList(testTask);
        when(taskService.getAllTasksById(eq(1L))).thenReturn(tasks);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/tasks/my-tasks")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTask.getDescription()))
                .andExpect(jsonPath("$[0].status").value(testTask.getStatus().toString()));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getOwnTasksShouldReturnNotFound() throws Exception {
        // Mock the service to throw an exception
        when(taskService.getAllTasksById(eq(1L))).thenThrow(new RuntimeException("Tasks not found"));

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/tasks/my-tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("An unexpected error occurred: Tasks not found"));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void createOwnTaskShouldCreateTask() throws Exception {
        // Mock the service to create a task
        NewTask newTask = new NewTask("Test Title", "Test Description", TaskStatus.PENDING);
        when(taskService.createNewTask(eq(1L), any(NewTask.class))).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task created successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void createOwnTaskShouldReturnBadRequest() throws Exception {
        // Mock the service to throw an exception
        NewTask newTask = new NewTask("", "", null); // Invalid data
        when(taskService.createNewTask(eq(1L), any(NewTask.class))).thenThrow(new IllegalArgumentException("Invalid input data"));

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"))
                .andExpect(jsonPath("$.description").value("Description is required"))
                .andExpect(jsonPath("$.status").value("Task status is required and cannot be null"));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void updateOwnTaskShouldUpdateTask() throws Exception {
        // Mock the service to update a task
        Task task = Mockito.spy(new Task("Updated Title", "Updated Description", TaskStatus.COMPLETED));
        when(task.getId()).thenReturn(1L);
        TaskDTO updatedTask = new TaskDTO(task);
        when(taskService.isTaskOwner(1L, EMAIL)).thenReturn(true);
        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/tasks/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task updated successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void updateOwnTaskShouldReturnForbidden() throws Exception {
        Task task = Mockito.spy(new Task("Updated Title", "Updated Description", TaskStatus.COMPLETED));
        when(task.getId()).thenReturn(1L);
        // Mock the service to return false for task ownership
        TaskDTO updatedTask = new TaskDTO(task);
        when(taskService.isTaskOwner(1L, EMAIL)).thenReturn(false);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/tasks/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You don't have permission to update this task"));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void deleteOwnTaskShouldDeleteTask() throws Exception {
        // Mock the service to delete a task
        when(taskService.isTaskOwner(1L, EMAIL)).thenReturn(true);
        when(taskService.deleteTask(1L)).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/tasks/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void deleteOwnTaskShouldReturnForbidden() throws Exception {
        // Mock the service to return false for task ownership
        when(taskService.isTaskOwner(1L, EMAIL)).thenReturn(false);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/tasks/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You don't have permission to delete this task"));
    }
}