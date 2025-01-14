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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// Doesn't use entities in controllers (not receive nor send)
// We use DTO to receive and send in controllers

@RestController
@RequestMapping("/api/user") //@PreAuthorize("hasRole('USER')")
public class EntityUserController { // This class also in the context of Spring Boot (has to be Component)
    // Dependencies Injection - Only things that are in the context of Spring Boot (has to be Component)
    // From behind generates a constructor and injects the bean for this repository (interface)
    @Autowired
    private EntityUserService entityUserService; // inject the interface directly
    // after make the Implementation this is no longer needed
    //private EntityUserRepository entityUserRepository;

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

    // if the user is authenticated: shows me the email
    @Operation(summary = "Get user's email (logged in)", description = "Return the email about the user authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's email retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User's email not found"),
            @ApiResponse(responseCode = "401", description = "Without authorization")
    })
    @GetMapping("/email")
    public String getEmail(Authentication authentication){
        return authentication.getName();
    }

    // Return a user by authentication
    @Operation(summary = "Get user's information (logged in)", description = "Return the information about the user authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Without authorization"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            EntityUserDTO user = entityUserService.getEntityUserDTOByEmail(getEmail(authentication));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update Username and Email from the authenticated user
    @Operation(summary = "Update user's information (logged in)", description = "Update your username and email")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "User's username and email updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @Valid @RequestBody UpdateEntityUserUsernameEmailDTO updatedUser) {
        try {
            entityUserService.updateEntityUserUsernameEmail(getAuthenticatedUserId(authentication), updatedUser);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) { // always before a RunTimeException that is general
            return new ResponseEntity<>("Invalid data provided: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Update Password from the authenticated user
    @Operation(summary = "Update user's password (logged in)", description = "Update your password")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "Your password has updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password")
    })
    @PutMapping("/profile/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @Valid @RequestBody UpdateEntityUserPasswordDTO updatedPassword) {
        try {
            entityUserService.updateEntityUserPassword(getAuthenticatedUserId(authentication), updatedPassword);
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) { // always before a RunTimeException that is general
            return new ResponseEntity<>("Invalid data provided: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Delete the authenticated user
    @Operation(summary = "Delete user (logged in)", description = "Delete my user that is authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid input data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEntityUser(Authentication authentication) {
        boolean deleted = entityUserService.deleteEntityUser(getAuthenticatedUserId(authentication));
        if (!deleted) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }
}
