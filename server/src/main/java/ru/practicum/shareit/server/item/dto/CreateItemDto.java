package ru.practicum.shareit.server.item.dto;

public record CreateItemDto(
        String name,
        String description,
        Long ownerId,
        Boolean available,
        Long requestId
) {
}