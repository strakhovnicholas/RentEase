package ru.practicum.shareit.gateway.core.item.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateItemDto(
        @NotBlank String name,
        @NotBlank String description,
        Long ownerId,
        @NotNull Boolean available,
        Long requestId) {
}

