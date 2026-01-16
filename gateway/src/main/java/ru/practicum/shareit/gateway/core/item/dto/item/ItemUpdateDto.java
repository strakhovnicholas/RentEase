package ru.practicum.shareit.gateway.core.item.dto.item;

import lombok.Builder;

@Builder
public record ItemUpdateDto(
        String name,
        String description,
        Boolean available) {
}

