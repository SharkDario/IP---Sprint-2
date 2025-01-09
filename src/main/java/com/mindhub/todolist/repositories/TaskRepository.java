package com.mindhub.todolist.repositories;

import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// <class Task, ID's type>
public interface TaskRepository extends JpaRepository<Task, Long> {
    // derived methods - findById, existsById, and countBy
    // The repository from JPA already have this method
    //Task findById(long id);
    Task findByTitle(String title);
    Task findByDescription(String description);
    Task findByUser(EntityUser user);
    List<Task> findByUserId(Long userId);

    boolean existsById(long id);
    boolean existsByTitle(String title);
    boolean existsByDescription(String description);
    boolean existsByUser(EntityUser user);

    int countById(long id);
    int countByTitle(String title);
    int countByDescription(String description);
    int countByUser(EntityUser user);

}
