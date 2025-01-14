package com.mindhub.todolist.dtos;

public record LoginRequest(
        String email,

        String password
) {
}
