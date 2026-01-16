package ru.practicum.shareit.gateway.core.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserCreateDto(String name, String password, @NotBlank @Email String email) {
}

