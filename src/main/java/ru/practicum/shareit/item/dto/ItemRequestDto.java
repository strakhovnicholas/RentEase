package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemRequestDto(@NotBlank String name, @NotBlank String description, Long ownerId,
                             @NotNull Boolean available) {
}

