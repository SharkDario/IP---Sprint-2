package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.repositories.EntityUserRepository;
import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.services.EntityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Doesn't use entities in controllers (not receive nor send)
// We use DTO to receive and send in controllers

@RestController
@RequestMapping("/api/user")
public class EntityUserController { // This class also in the context of Spring Boot (has to be Component)
    // Dependencies Injection - Only things that are in the context of Spring Boot (has to be Component)
    // From behind generates a constructor and injects the bean for this repository (interface)
    @Autowired
    private EntityUserService entityUserService; // inject the interface directly
    // after make the Implementation this is no longer needed
    //private EntityUserRepository entityUserRepository;

    @GetMapping("/{id}")
    public EntityUserDTO getUserById(@PathVariable Long id) {
        // after make the Services and Implement it's no longer needed
        //return new EntityUserDTO(entityUserRepository.findById(id).orElse(null));
        return entityUserService.getEntityUserDTOById(id); // after the implementation
    }

    // Record example
    @PostMapping
    public ResponseEntity<?> createEntityUser(@RequestBody NewEntityUser newEntityUser) {
        // after make the Services and Implement it's no longer needed
        // EntityUser entityUser = new EntityUser(newEntityUser.username(), newEntityUser.password(), newEntityUser.email());
        // entityUserRepository.save(entityUser);
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }
}
