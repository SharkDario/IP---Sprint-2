package com.mindhub.todolist.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mindhub.todolist.models.EntityUser;

import java.util.List;

// Data Transfer Object - transfer data with the back and front for example
public class EntityUserDTO {
    // attributes from the class
    // only the ones we want to see (Not password for example)
    // final = constant (doesn't change)
    // @JsonProperty - id in the response but not in the petition
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Long id;

    private String username, email;

    private List<TaskDTO> tasks;

    // MapStruct: we can use Libraries for mappers (annotation, mapping from an object to another)
    // Package from utils: build it from scratch

    // Constructor
    public EntityUserDTO(EntityUser entityUser) {
        id = entityUser.getId();
        username = entityUser.getUsername();
        email = entityUser.getEmail();
        tasks = entityUser // Object type EntityUser
                .getTasks() // Set<Task>
                .stream() // Stream<Task>
                .map(task -> new TaskDTO(task)) // Stream<TaskDTO> Function Lambda
                .toList(); // List<TaskDTO>
    }
    // .map( TaskDTO::new ) Short form - Function Lambda
    // .map(task -> {
    //          System.out.println("test");
    //          return new TaskDTO(task)})

    // Generate the getters to see the JSON
    // Without getters, Jackson doesn't capture the information

    // Jackson returns the JSON with the keys named like the word after "get"
    // For example, getUsername -> key: username
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }
}
