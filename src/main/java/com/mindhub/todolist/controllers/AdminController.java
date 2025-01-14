package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.dtos.UpdateEntityUserPasswordDTO;
import com.mindhub.todolist.dtos.UpdateEntityUserUsernameEmailDTO;
import com.mindhub.todolist.services.EntityUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private EntityUserService entityUserService;

    // List all users
    @Operation(summary = "Get all users", description = "Return the information about all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Users not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/users")
    public ResponseEntity<List<EntityUserDTO>> getAllUsers() {
        List<EntityUserDTO> users = entityUserService.getAllEntityUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Return a user by id
    @Operation(summary = "Get a user by ID", description = "Return the information about a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/user/{id}")
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
    @PostMapping("/user")
    public ResponseEntity<?> createEntityUser(@Valid @RequestBody NewEntityUser newEntityUser) {
        entityUserService.registerUser(newEntityUser);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    // Create a ADMIN user
    @Operation(summary = "Create an admin", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid input data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody NewEntityUser newEntityUser) {
        entityUserService.registerAdminUser(newEntityUser);
        return new ResponseEntity<>("Admin created successfully", HttpStatus.CREATED);
    }

    // Update Username and Email - User
    @Operation(summary = "Update user's information", description = "Update a user's username and email")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "User's username and email updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/user/{id}")
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
    @PutMapping("/user/{id}/password")
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
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteEntityUser(@PathVariable Long id) {
        boolean deleted = entityUserService.deleteEntityUser(id);
        if (!deleted) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

}

/*
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/protected")
    @SecurityRequirement(name = "bearerAuth") // Apply JWT authentication to this endpoint
    public String protectedEndpoint() {
        return "This is a protected endpoint";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint";
    }
}
*/