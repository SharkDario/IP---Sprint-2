package com.mindhub.todolist.models;

import jakarta.persistence.*;

// Table in the DB
@Entity
public class EntityUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    // Constructor
    public EntityUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    // Empty Constructor
    public EntityUser() {}

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
