package ru.practicum.shareit.gateway.core.user.dto;

import lombok.Builder;

@Builder
public record UserUpdateDto(String name, String email) {
}

