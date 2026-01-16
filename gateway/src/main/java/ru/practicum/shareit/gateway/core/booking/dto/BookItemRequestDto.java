package ru.practicum.shareit.gateway.core.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Builder;

@Builder
public record BookItemRequestDto (
        long itemId,
        @FutureOrPresent
        LocalDateTime start,
        @Future
        LocalDateTime end
){}