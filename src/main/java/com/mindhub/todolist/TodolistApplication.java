package com.mindhub.todolist;

import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.repositories.EntityUserRepository;
import com.mindhub.todolist.repositories.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class TodolistApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodolistApplication.class, args);
	}

	// Bean: Object that Spring Boot creates from the start of the application
	// We need to be clear with the names like entityUserRepository
	@Bean
	public CommandLineRunner initData(EntityUserRepository entityUserRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// password need to be at least 8 characters
			EntityUser user = new EntityUser("Dario7", passwordEncoder.encode("12345678"), "mdarioc1998@gmail.com");
			// ID = null because the user isn't saved
			//System.out.println(user.toString());
			// Always verify the order in the saves
			entityUserRepository.save(user);
			// ID = 1 because the user's saved
			System.out.println(user);

			// It's needed an empty constructor to recreate the object from the DB
			// User that exists
			// "Finds" in EntityUserRepository
			EntityUser user2 = entityUserRepository.findById(1l).orElse(null);
			System.out.println(user2);
			//user2 = entityUserRepository.findByEmail("mdarioc1998@gmail.com");
			System.out.println(user2);
			user2 = entityUserRepository.findByUsername("Dario7");
			System.out.println(user2);
			user2 = entityUserRepository.findByUsernameAndPassword("Dario7", "12345678");
			// "Exists"
			// "Count"

			// User that doesn't exist
			user2 = entityUserRepository.findById(2l).orElse(null);
			System.out.println(user2);
			//user2 = entityUserRepository.findByEmail("test@gmail.com");
			System.out.println(user2);
			user2 = entityUserRepository.findByUsername("Test7");
			System.out.println(user2);
			user2 = entityUserRepository.findByUsernameAndPassword("Test7", "01234");
			System.out.println(user2);
			// "Exists"
			// "Count"

			Task task = new Task("Sprint 2 - Activity", "Create Entities, Models, DTOs, Repositories, Controllers and Services; Implement CRUD", TaskStatus.IN_PROGRESS);
			// Before save the task we need to add the task to the user
			user.addTask(task);
			System.out.println(task);
			taskRepository.save(task);
			System.out.println(task);

			user = new EntityUser("Dario8", passwordEncoder.encode("12345678"), "dario@gmail.com");
			// ID = null because the user isn't saved
			//System.out.println(user.toString());
			// Always verify the order in the saves
			entityUserRepository.save(user);
			task = new Task("Sprint 3 - Activity", "Implement Spring Security (ADMIN and USER authorities)", TaskStatus.IN_PROGRESS);
			// Before save the task we need to add the task to the user
			user.addTask(task);
			taskRepository.save(task);

			user = new EntityUser("Shark", passwordEncoder.encode("12345678"), "shark@gmail.com");
			//admin
			user.setRole(RoleType.ADMIN);
			entityUserRepository.save(user);

		};
	}
}
