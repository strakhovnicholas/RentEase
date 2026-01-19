package ru.practicum.shareit.gateway.core.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BookItemRequestDto(
        @NotNull(message = "Item ID cannot be null")
        Long itemId,

        @NotNull(message = "Start time cannot be null")
        @FutureOrPresent(message = "Start time must be in the present or future")
        LocalDateTime start,

        @NotNull(message = "End time cannot be null")
        @Future(message = "End time must be in the future")
        LocalDateTime end
) {
}