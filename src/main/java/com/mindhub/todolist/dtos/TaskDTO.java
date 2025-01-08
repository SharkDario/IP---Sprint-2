package com.mindhub.todolist.dtos;

import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;

public class TaskDTO {

    private Long id;

    private String title, description;

    private TaskStatus status;

    // Constructor
    public TaskDTO(Task task) {
        id = task.getId();
        title = task.getTitle();
        description = task.getDescription();
        status = task.getStatus();
    }

    // Generate only the getters
    // Jackson returns the JSON with the keys named like the word after "get"
    // For example, getUsername -> key: username
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
