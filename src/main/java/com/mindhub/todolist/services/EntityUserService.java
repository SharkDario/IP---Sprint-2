package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.dtos.UpdateEntityUserPasswordDTO;
import com.mindhub.todolist.dtos.UpdateEntityUserUsernameEmailDTO;
import com.mindhub.todolist.models.EntityUser;
import java.util.List;

public interface EntityUserService {
    // only declare methods because it's an interface
    EntityUserDTO getEntityUserDTOById(Long id);

    EntityUser getEntityUserById(Long id);

    EntityUser saveEntityUser(EntityUser entityUser);

    void createNewEntityUser(NewEntityUser newEntityUser);

    public List<EntityUserDTO> getAllEntityUsers();
    //void deleteEntityUserById(Long id);

    public boolean existsByEmail(String email);

    boolean updateEntityUserUsernameEmail(Long id, UpdateEntityUserUsernameEmailDTO updatedEntityUser);

    boolean updateEntityUserPassword(Long id, UpdateEntityUserPasswordDTO updatedPassword);

    public boolean deleteEntityUser(Long id);

}
