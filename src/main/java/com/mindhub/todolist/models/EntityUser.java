package com.mindhub.todolist.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

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

    private RoleType role = RoleType.USER;

    // mappedBy points to the attribute "user" in Task
    // with Set (instead of List) we have the data without repetitions (happen sometimes with List)
    // for default is LAZY in fetch
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    // @ElementCollection Simulation for a OneToMany relation, automatic
    // Limitation: if I want to change the list, it needs to be deleted and created again
    // Doesn't need: Entity, Repository, Relations
    // Simple data like telephones

    // Constructor - Responsibility to create a EntityUser (not a relation)
    public EntityUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    // Empty Constructor - Parsing the object
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

    public Set<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        task.setUser(this); // DB
        tasks.add(task); // implicit this. - Not save the reference
    }

    public void removeTask(Task task) {
        task.setUser(null); // DB
        tasks.remove(task); // implicit this. - Not save the reference
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    // Polymorphism
    @Override
    public String toString() {
        return "EntityUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
