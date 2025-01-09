package com.mindhub.todolist.dtos;

import com.mindhub.todolist.models.TaskStatus;

public record NewTask(String title, String description, TaskStatus status) {
}
