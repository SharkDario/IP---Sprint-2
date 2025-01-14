package com.mindhub.todolist.repositories;

import com.mindhub.todolist.models.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// <class EntityUser, ID's type
// @Repository - It's not necessary because JpaRepository already have @Repository from its extensions
// a Repository is a component, that moves the information to the DB and brings it from the DB
// <Generic>
public interface EntityUserRepository extends JpaRepository<EntityUser, Long> {
    // query's - @Query() with native query
    // derived methods - findById, existsById, and countBy

    // The repository from JPA already have this method
    //EntityUser findById(long id);
    EntityUser findByUsername(String username);
    Optional<EntityUser> findByEmail(String email);
    EntityUser findByUsernameAndPassword(String username, String password);

    boolean existsById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndPassword(String email, String password);
    // Update validation
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByUsernameAndIdNot(String username, Long id);

    int countById(long id);
    int countByUsername(String username);
    int countByEmail(String email);
    int countByUsernameAndPassword(String username, String password);
}
