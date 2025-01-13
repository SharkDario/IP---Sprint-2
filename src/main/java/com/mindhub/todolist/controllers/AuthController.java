package com.mindhub.todolist.controllers;

import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dtos.NewEntityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody NewEntityUser loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        // set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // generate the token
        String jwt = jwtUtil.generateToken(authentication.getName());
        // return the token
        return ResponseEntity.ok(jwt);
    }

    // Register
}