package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.*;
import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.services.EntityUserService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Doesn't use entities in controllers (not receive nor send)
// We use DTO to receive and send in controllers

@RestController
@RequestMapping("/api/user")
public class EntityUserController { // This class also in the context of Spring Boot (has to be Component)
    // Dependencies Injection - Only things that are in the context of Spring Boot (has to be Component)
    // From behind generates a constructor and injects the bean for this repository (interface)
    @Autowired
    private EntityUserService entityUserService; // inject the interface directly
    // after make the Implementation this is no longer needed
    //private EntityUserRepository entityUserRepository;

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

    // List all users
    @Operation(summary = "Get all users", description = "Return the information about all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Users not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping
    public ResponseEntity<List<EntityUserDTO>> getAllUsers() {
        List<EntityUserDTO> users = entityUserService.getAllEntityUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Return a user by id
    @Operation(summary = "Get a user by ID", description = "Return the information about a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            EntityUserDTO user = entityUserService.getEntityUserDTOById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create a user
    @Operation(summary = "Create a user", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid input data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<?> createEntityUser(@Valid @RequestBody NewEntityUser newEntityUser) {
        entityUserService.createNewEntityUser(newEntityUser);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    // Update Username and Email - User
    @Operation(summary = "Update user's information", description = "Update a user's username and email")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "User's username and email updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntityUser(@PathVariable Long id, @Valid @RequestBody UpdateEntityUserUsernameEmailDTO updatedUser) {
        try {
            entityUserService.updateEntityUserUsernameEmail(id, updatedUser);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) { // always before a RunTimeException that is general
            return new ResponseEntity<>("Invalid data provided: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Update Password - User
    @Operation(summary = "Update user's password", description = "Update a user's password")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "User's password updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid password")
    })
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdateEntityUserPasswordDTO updatedPassword) {
        try {
            entityUserService.updateEntityUserPassword(id, updatedPassword);
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) { // always before a RunTimeException that is general
            return new ResponseEntity<>("Invalid data provided: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Delete a user
    @Operation(summary = "Delete a user", description = "Delete a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid input data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntityUser(@PathVariable Long id) {
        boolean deleted = entityUserService.deleteEntityUser(id);
        if (!deleted) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }
}
