package ru.practicum.shareit.server.user.dto;

import ru.practicum.shareit.server.user.enums.UserRole;

public record UserCreateDto(String name, String password, String email, UserRole role) {
}

