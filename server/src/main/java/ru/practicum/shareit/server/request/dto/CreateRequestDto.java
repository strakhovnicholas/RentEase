package ru.practicum.shareit.server.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRequestDto(
        @NotBlank
        @Size(max = 100)
        String description
) {}