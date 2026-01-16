package ru.practicum.shareit.server.user.dto;


import ru.practicum.shareit.server.user.enums.UserRole;

public record UserResponseDto(Long id, String name, String email, UserRole role) {
}

