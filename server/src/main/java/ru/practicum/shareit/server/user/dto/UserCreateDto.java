package ru.practicum.shareit.server.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.practicum.shareit.server.user.enums.UserRole;

public record UserCreateDto(String name, String password, @NotBlank @Email String email, UserRole role) {
}

