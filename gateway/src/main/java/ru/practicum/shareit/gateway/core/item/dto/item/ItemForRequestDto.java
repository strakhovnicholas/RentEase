package ru.practicum.shareit.gateway.core.item.dto.item;

import lombok.Builder;

@Builder
public record ItemForRequestDto(
        Long id,
        String name,
        String description,
        Boolean available,
        Long ownerId,
        Long requestI
) {}
