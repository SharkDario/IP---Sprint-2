package com.mindhub.todolist.dtos;

import com.mindhub.todolist.models.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewTask(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Task status is required and cannot be null")
        TaskStatus status
        ) {
}
