package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewTask;
import com.mindhub.todolist.dtos.TaskDTO;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.services.EntityUserService;
import com.mindhub.todolist.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TaskController: for "USER"
@RestController
@RequestMapping("/api")
public class UserTaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private EntityUserService entityUserService;

    private Long getAuthenticatedUserId(Authentication authentication) {
        EntityUserDTO user = entityUserService.getEntityUserDTOByEmail(authentication.getName());
        return user.getId();
    }

    // Validate errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
    // Validate business exceptions
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Map<String, String> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    // Validate general exceptions
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleGeneralExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + ex.getMessage());
        return error;
    }

    // User's endpoints
    // User's tasks
    @Operation(summary = "Get all user's tasks (user logged in)", description = "Return all tasks associated with the user (logged in)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Tasks not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/user/tasks/my-tasks")
    public ResponseEntity<List<TaskDTO>> getOwnTasks(Authentication authentication) {
        List<TaskDTO> tasks = taskService.getAllTasksById(getAuthenticatedUserId(authentication));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // User can create own tasks
    @Operation(summary="Create a task (user logged in)", description = "Create a new task for the user (logged in)")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid input data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/user")
    public ResponseEntity<?> createOwnTask(@Valid @RequestBody NewTask newTask, Authentication authentication) {
        try {
            taskService.createNewTask(getAuthenticatedUserId(authentication), newTask);
            return new ResponseEntity<>("Task created successfully", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Update a task (user logged in)", description = "Update a task by its ID")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/user/tasks/{id}") //@PreAuthorize("@taskService.isTaskOwner(#id, authentication.name)") -> this: @EnableMethodSecurity (in SecurityConfig) enables @PreAuthorize
    public ResponseEntity<?> updateOwnTask(@PathVariable Long id, @Valid @RequestBody TaskDTO updatedTask, Authentication authentication) {
        try {
            if(!taskService.isTaskOwner(id, authentication.getName())) {
                return new ResponseEntity<>("You don't have permission to update this task", HttpStatus.FORBIDDEN);
            }
            taskService.updateTask(id, updatedTask);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Delete a task (user logged in)", description = "Delete a task by its ID")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @DeleteMapping("/user/tasks/{id}")
    public ResponseEntity<?> deleteOwnTask(@PathVariable Long id, Authentication authentication) {
        try {
            if(!taskService.isTaskOwner(id, authentication.getName())) {
                return new ResponseEntity<>("You don't have permission to delete this task", HttpStatus.FORBIDDEN);
            }
            taskService.deleteTask(id);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
