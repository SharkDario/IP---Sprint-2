package com.mindhub.todolist.services.impl;

import com.mindhub.todolist.dtos.EntityUserDTO;
import com.mindhub.todolist.dtos.NewTask;
import com.mindhub.todolist.dtos.TaskDTO;
import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.repositories.EntityUserRepository;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EntityUserRepository entityUserRepository;

    @Override
    public TaskDTO getTaskDTOById(Long id) {
        return new TaskDTO(getTaskById(id));
    }
    //orElseThrow. ListBlank verify that isn't empty and not a blank space
    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task with ID " + id + " not found"));
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public boolean createNewTask(Long userId, NewTask newTask) {
        EntityUser user = entityUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
        // Create the task
        Task task = new Task(newTask.title(), newTask.description(), newTask.status());
        // Associate the user
        task.setUser(user);
        // Save the task
        saveTask(task);
        return true;
    }

    @Override
    public List<TaskDTO> getAllTasksById(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks.stream().map(TaskDTO::new).toList();
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTaskById(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    @Override
    public boolean updateTask(Long id, TaskDTO updatedTask) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task with ID " + id + " not found"));
        // Updating the task
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        // Saving the task
        taskRepository.save(existingTask);
        return true;
    }

    @Override
    public boolean deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
        return true;
    }

    @Override
    public boolean isTaskOwner(Long taskId, String userEmail) {
        Task task = getTaskById(taskId);
        // Get the task owner email
        String taskOwnerEmail = task.getUser().getEmail();
        // Compare the emails
        return taskOwnerEmail.equals(userEmail);
    }
}
