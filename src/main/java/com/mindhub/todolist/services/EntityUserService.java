package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.models.EntityUser;

public interface EntityUserService {
    // only declare methods because it's an interface
    EntityUserDTO getEntityUserDTOById(Long id);

    EntityUser getEntityUserById(Long id);

    EntityUser saveEntityUser(EntityUser entityUser);

    void createNewEntityUser(NewEntityUser newEntityUser);
}
