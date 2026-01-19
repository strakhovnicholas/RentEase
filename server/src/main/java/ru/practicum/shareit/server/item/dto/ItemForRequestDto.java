package ru.practicum.shareit.server.item.dto;

public record ItemForRequestDto(
        Long id,
        String name,
        String description,
        Boolean available,
        Long ownerId,
        Long requestI
) {}
