package ru.practicum.shareit.server.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateItemDto(
        @NotBlank String name,
        @NotBlank String description,
        Long ownerId,
        @NotNull Boolean available,
        Long requestId) {
}

