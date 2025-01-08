package com.mindhub.todolist.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

// Table in the DB
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title, description;

    private TaskStatus status;

    @ManyToOne
    private EntityUser user;

    // Constructor - Responsibility to create a Task (not create a relation)
    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Empty Constructor
    public Task() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    //@JsonIgnore // When generate the Json, Jackson ignores this method, doesn't call the object user
    // When we want to know who user created this task, we can't with this tag
    public EntityUser getUser() {
        return user;
    }
    // method to create the relation
    public void setUser(EntityUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", user=" + user +
                '}';
    }
}
