package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.TaskDTO;
import com.mindhub.todolist.dtos.NewTask;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.RoleType;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminTaskController.class)
public class AdminTaskControllerTest {

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

    private TaskDTO testTask;
    private EntityUserDTO testUser;
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

        EntityUser user = Mockito.mock(EntityUser.class);
        when(user.getId()).thenReturn(2L);
        when(user.getUsername()).thenReturn("Dario7");
        when(user.getEmail()).thenReturn("dario@gmail.com");
        when(user.getRole()).thenReturn(RoleType.USER);

        when(task.getUser()).thenReturn(user);
        user.addTask(task);
        testTask = new TaskDTO(task);
        testUser = new EntityUserDTO(user);

        EntityUser adminUser = Mockito.mock(EntityUser.class);
        when(adminUser.getId()).thenReturn(1L);
        when(adminUser.getUsername()).thenReturn("Miguel7");
        when(adminUser.getEmail()).thenReturn(EMAIL);
        when(adminUser.getRole()).thenReturn(RoleType.ADMIN);

        EntityUserDTO adminUserDTO = new EntityUserDTO(adminUser);
        when(entityUserService.getEntityUserDTOByEmail(EMAIL)).thenReturn(adminUserDTO);
        when(entityUserService.getEntityUserDTOById(1L)).thenReturn(adminUserDTO);

        EntityUserDTO regularUserDTO = new EntityUserDTO(user);
        when(entityUserService.getEntityUserDTOById(2L)).thenReturn(regularUserDTO);

        token = jwtUtil.generateToken(EMAIL);
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getAllTasksByIdShouldReturnTasksFromUser() throws Exception {
        // Mock the service to return a list of tasks
        List<TaskDTO> tasks =  Collections.singletonList(testTask);
        when(taskService.getAllTasksById(eq(2L))).thenReturn(tasks);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/tasks/users/{userId}/tasks", 2L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTask.getDescription()))
                .andExpect(jsonPath("$[0].status").value(testTask.getStatus().toString()));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void getAllTasksShouldReturnAllTasks() throws Exception {
        // Mock the service to return a list of tasks
        List<TaskDTO> tasks = Collections.singletonList(testTask);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTask.getDescription()))
                .andExpect(jsonPath("$[0].status").value(testTask.getStatus().toString()));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void getTaskByIdShouldReturnTask() throws Exception {
        // Mock the service to return a task
        when(taskService.getTaskDTOById(1L)).thenReturn(testTask);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/tasks/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.title").value(testTask.getTitle()))
                .andExpect(jsonPath("$.description").value(testTask.getDescription()))
                .andExpect(jsonPath("$.status").value(testTask.getStatus().toString()));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void getTaskByIdShouldReturnNotFound() throws Exception {
        // Mock the service to throw an exception
        when(taskService.getTaskDTOById(1L)).thenThrow(new RuntimeException("Task not found"));

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/tasks/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void createTaskShouldCreateTask() throws Exception {
        // Mock the service to create a task
        NewTask newTask = new NewTask("Test Title", "Test Description", TaskStatus.PENDING);
        when(taskService.createNewTask(eq(2L), any(NewTask.class))).thenReturn(true);
        when(entityUserService.getEntityUserDTOById(2L)).thenReturn(testUser);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/tasks/user/{userId}", 2L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task created successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void createTaskShouldReturnForbiddenForAdminUser() throws Exception {
        // Mock the service to throw an exception for admin user
        NewTask newTask = new NewTask("Test Title", "Test Description", TaskStatus.PENDING);
        EntityUser user = new EntityUser("Miguel7", "12345678", EMAIL);
        user.setRole(RoleType.ADMIN);
        when(entityUserService.getEntityUserDTOById(1L)).thenReturn(new EntityUserDTO(user));

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/tasks/user/{userId}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You can't create a task for an administrator"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void updateTaskShouldUpdateTask() throws Exception {
        // Mock the service to update a task
        Task task = new Task("Updated Title", "Updated Description", TaskStatus.COMPLETED);
        TaskDTO updatedTask = new TaskDTO(task);
        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/tasks/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task updated successfully"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "ADMIN")
    void deleteTaskShouldDeleteTask() throws Exception {
        // Mock the service to delete a task
        when(taskService.deleteTask(1L)).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/tasks/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted successfully"));
    }

}