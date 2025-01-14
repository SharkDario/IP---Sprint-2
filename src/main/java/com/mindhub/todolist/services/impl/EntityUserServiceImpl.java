package com.mindhub.todolist.services.impl;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.dtos.UpdateEntityUserPasswordDTO;
import com.mindhub.todolist.dtos.UpdateEntityUserUsernameEmailDTO;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.repositories.EntityUserRepository;
import com.mindhub.todolist.services.EntityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// with @Service the implementation is in the context of Spring Boot
// and a component
@Service
public class EntityUserServiceImpl implements EntityUserService {
    @Autowired
    private EntityUserRepository entityUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public EntityUserDTO getEntityUserDTOById(Long id) {
        return new EntityUserDTO(getEntityUserById(id));
    }

    @Override
    public EntityUserDTO getEntityUserDTOByEmail(String email) {
        return new EntityUserDTO(getEntityUserByEmail(email));
    }

    @Override
    public EntityUser getEntityUserById(Long id) {
        return entityUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
    }

    @Override
    public EntityUser getEntityUserByEmail(String email) {
        return entityUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
    }

    @Override
    public EntityUser saveEntityUser(EntityUser entityUser) {
        //EntityUser entityUser = new EntityUser(newEntityUser.username(), newEntityUser.password(), newEntityUser.email());
        return entityUserRepository.save(entityUser);
    }

    @Override
    public void registerAdminUser(NewEntityUser newEntityUser) {
        validateEntityUser(newEntityUser);
        EntityUser entityUser = new EntityUser(newEntityUser.username(), passwordEncoder.encode(newEntityUser.password()), newEntityUser.email());
        entityUser.setRole(RoleType.ADMIN);
        saveEntityUser(entityUser);
    }

    public void registerUser(NewEntityUser newEntityUser) {
        validateEntityUser(newEntityUser);
        EntityUser entityUser = new EntityUser(newEntityUser.username(), passwordEncoder.encode(newEntityUser.password()), newEntityUser.email());
        saveEntityUser(entityUser);
    }

    public void validateEntityUser(NewEntityUser newEntityUser) {
        // Validate unique email
        if (entityUserRepository.existsByEmail(newEntityUser.email())) {
            throw new IllegalArgumentException("The email " + newEntityUser.email() + " is already in use.");
        }
        // Validate unique username
        if (entityUserRepository.existsByUsername(newEntityUser.username())) {
            throw new IllegalArgumentException("The username " + newEntityUser.username() + " is already in use.");
        }
    }

    public List<EntityUserDTO> getAllEntityUsers() {
        return entityUserRepository.findAll().stream()
                .map(EntityUserDTO::new)
                .collect(Collectors.toList());
    }

    public boolean existsByEmail(String email) {
        return entityUserRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean updateEntityUserUsernameEmail(Long id, UpdateEntityUserUsernameEmailDTO updatedEntityUser) {
        EntityUser entityUser = getEntityUserById(id);
        // Validate unique email and username
        if(!updatedEntityUser.email().equals(entityUser.getEmail()) &&
            entityUserRepository.existsByEmailAndIdNot(updatedEntityUser.email(), id)) {
            throw new IllegalArgumentException("The email " + updatedEntityUser.email() + " is already in use.");
        }
        if(!updatedEntityUser.username().equals(entityUser.getUsername()) &&
            entityUserRepository.existsByUsernameAndIdNot(updatedEntityUser.username(), id)) {
            throw new IllegalArgumentException("The username " + updatedEntityUser.username() + " is already in use.");
        }
        // Update the user
        entityUser.setUsername(updatedEntityUser.username());
        entityUser.setEmail(updatedEntityUser.email());
        entityUserRepository.save(entityUser);
        return true;
    }

    @Override
    public boolean updateEntityUserPassword(Long id, UpdateEntityUserPasswordDTO updatedPassword) {
        EntityUser entityUser = getEntityUserById(id);
        // passwordEncoder.matches() to validate the old password
        if (!passwordEncoder.matches(updatedPassword.oldPassword(), entityUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // Encode the new password before saving
        entityUser.setPassword(passwordEncoder.encode(updatedPassword.newPassword()));
        entityUserRepository.save(entityUser);
        return true;
    }
    /* public boolean updateEntityUser(Long id, EntityUserDTO updatedUser) {
        EntityUser entityUser = entityUserRepository.findById(id).orElse(null);
        if (entityUser == null) {
            return false;
        }
        entityUser.setUsername(updatedUser.getUsername());
        //entityUser.setPassword(updatedUser.getPassword());
        entityUser.setEmail(updatedUser.getEmail());
        entityUserRepository.save(entityUser);
        return true;
    }*/

    public boolean deleteEntityUser(Long id) {
        if (!entityUserRepository.existsById(id)) {
            return false;
        }
        entityUserRepository.deleteById(id);
        return true;
    }
}
