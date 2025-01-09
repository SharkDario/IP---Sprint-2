package com.mindhub.todolist.services.impl;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.repositories.EntityUserRepository;
import com.mindhub.todolist.services.EntityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// with @Service the implementation is in the context of Spring Boot
// and a component
@Service
public class EntityUserServiceImpl implements EntityUserService {
    @Autowired
    private EntityUserRepository entityUserRepository;

    @Override
    public EntityUserDTO getEntityUserDTOById(Long id) {
        return new EntityUserDTO(getEntityUserById(id));
    }

    @Override
    public EntityUser getEntityUserById(Long id) {
        return entityUserRepository.findById(id).orElse(null);
    }

    @Override
    public EntityUser saveEntityUser(EntityUser entityUser) {
        //EntityUser entityUser = new EntityUser(newEntityUser.username(), newEntityUser.password(), newEntityUser.email());
        return entityUserRepository.save(entityUser);
    }

    @Override
    public void createNewEntityUser(NewEntityUser newEntityUser) {
        // if returns an exception or not: validateEntityUser(newEntityUser);
        EntityUser entityUser = new EntityUser(newEntityUser.username(), newEntityUser.password(), newEntityUser.email());
        saveEntityUser(entityUser);
    }

    public void validateEntityUser(NewEntityUser newEntityUser) {

    }
}
