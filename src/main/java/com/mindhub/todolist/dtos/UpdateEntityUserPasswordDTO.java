package com.mindhub.todolist.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEntityUserPasswordDTO(
        @NotBlank(message = "Old password is required")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String newPassword
        ) {
}
