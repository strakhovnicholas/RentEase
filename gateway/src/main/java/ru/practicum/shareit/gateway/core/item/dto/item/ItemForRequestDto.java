package ru.practicum.shareit.gateway.core.item.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ItemForRequestDto(
        @Positive(message = "Item ID must be positive")
        Long id,

        @NotBlank(message = "Item name cannot be empty")
        @Size(min = 1, max = 255, message = "Item name must be between 1 and 255 characters")
        String name,

        @NotBlank(message = "Item description cannot be empty")
        @Size(min = 1, max = 1000, message = "Item description must be between 1 and 1000 characters")
        String description,

        @NotNull(message = "Available status cannot be null")
        Boolean available,

        Long ownerId,

        Long requestId
) {
}