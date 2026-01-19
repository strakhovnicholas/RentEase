package ru.practicum.shareit.gateway.core.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateRequestDto(
        @NotBlank
        @Size(max = 100)
        String description
) {}