package com.mindhub.todolist.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskDTO {
    // @JsonProperty - id in the response but not in the petition (to not use @JsonIgnore)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Task status is required and cannot be null")
    private TaskStatus status;

    //private EntityUser user;

    // Constructor
    public TaskDTO(Task task) {
        id = task.getId();
        title = task.getTitle();
        description = task.getDescription();
        status = task.getStatus();
    }

    // Empty Constructor
    public TaskDTO() {
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

    //public EntityUser getUser() { return user; }
}
