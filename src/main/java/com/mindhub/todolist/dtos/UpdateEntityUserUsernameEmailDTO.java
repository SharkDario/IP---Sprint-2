package com.mindhub.todolist.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEntityUserUsernameEmailDTO(
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 10, message = "Username must be between 4 and 10 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
        ) {
}
