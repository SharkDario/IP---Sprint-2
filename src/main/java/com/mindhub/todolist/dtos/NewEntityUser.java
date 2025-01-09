package com.mindhub.todolist.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Creates an Object (immutable)
public record NewEntityUser(
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 10, message = "Username must be between 4 and 10 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
        ) {
}
