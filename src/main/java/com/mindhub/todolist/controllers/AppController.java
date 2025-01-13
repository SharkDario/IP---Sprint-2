package com.mindhub.todolist.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class AppController {
    // if the user is authenticated: shows me the email
    @GetMapping("/name")
    public String getUserName(Authentication authentication){
        return authentication.getName();
    }

}