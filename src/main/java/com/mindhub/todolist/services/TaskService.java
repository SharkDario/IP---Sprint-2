package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewEntityUser;
import com.mindhub.todolist.dtos.NewTask;
import com.mindhub.todolist.dtos.TaskDTO;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.Task;

import java.util.List;

public interface TaskService {
    // only declare methods because it's an interface
    TaskDTO getTaskDTOById(Long id);

    Task getTaskById(Long id);

    Task saveTask(Task task);

    void createNewTask(Long userId, NewTask newTask);

    public List<TaskDTO> getAllTasks(Long userId);

    void deleteTaskById(Long id);

    //public boolean existsByEmail(String email);

    public boolean updateTask(Long id, TaskDTO updatedTask);

    public boolean deleteTask(Long id);
}
