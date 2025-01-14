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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
// TaskController: for "ADMIN"
@RestController
@RequestMapping("/api/admin/tasks")
public class AdminTaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private EntityUserService entityUserService;

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

    // Admin endpoints

    // Get all user's tasks by user's ID
    @Operation(summary = "Get all tasks for a user", description = "Return all tasks associated with a specific user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Tasks not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasksById(@PathVariable Long userId) {
        List<TaskDTO> tasks = taskService.getAllTasksById(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // List all tasks
    @Operation(summary = "Get all tasks", description = "Return the information about all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Tasks not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Get a Task by ID
    @Operation(summary = "Get a task by ID", description = "Return the information about a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            TaskDTO task = taskService.getTaskDTOById(id);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean validateUser(Long id) {
        EntityUserDTO user = entityUserService.getEntityUserDTOById(id);
        if (user.getRole().equals(RoleType.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't create a task for an administrator");
        }
        return true;
    }

    // Create a task
    @Operation(summary="Create a task", description = "Create a new task for a specific user")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid input data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("user/{userId}")
    public ResponseEntity<?> createTask(@PathVariable Long userId, @Valid @RequestBody NewTask newTask) {
        try {
            if (validateUser(userId)) {
                taskService.createNewTask(userId, newTask);
                return new ResponseEntity<>("Task created successfully", HttpStatus.CREATED);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Update a task
    @Operation(summary = "Update a task", description = "Update a task by its ID")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO updatedTask) {
        try {
            taskService.updateTask(id, updatedTask);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
    // Delete a Task
    @Operation(summary = "Delete a task", description = "Delete a task by its ID")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /*
    // Admin endpoints - manage all tasks
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() { ... }

    @PostMapping("/assign/{userId}")
    public ResponseEntity<?> assignTaskToUser(@PathVariable Long userId,
                                              @Valid @RequestBody NewTask newTask) { ... }


    // Get all user's tasks by user's ID
    @Operation(summary = "Get all tasks for a user", description = "Return all tasks associated with a specific user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Tasks not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/admin/{userId}")
    public ResponseEntity<List<TaskDTO>> getAllTasksById(@PathVariable Long userId) {
        List<TaskDTO> tasks = taskService.getAllTasksById(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // List all tasks
    @Operation(summary = "Get all tasks", description = "Return the information about all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Tasks not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/admin")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Get a Task by ID
    @Operation(summary = "Get a task by ID", description = "Return the information about a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            TaskDTO task = taskService.getTaskDTOById(id);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create a task
    @Operation(summary="Create a task", description = "Create a new task for a specific user")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid input data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/admin/{userId}")
    public ResponseEntity<?> createTask(@PathVariable Long userId, @Valid @RequestBody NewTask newTask) {
        try {
            taskService.createNewTask(userId, newTask);
            return new ResponseEntity<>("Task created successfully", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
    // Update a task
    @Operation(summary = "Update a task", description = "Update a task by its ID")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO updatedTask) {
        try {
            taskService.updateTask(id, updatedTask);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
    // Delete a Task
    @Operation(summary = "Delete a task", description = "Delete a task by its ID")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

     */
}
