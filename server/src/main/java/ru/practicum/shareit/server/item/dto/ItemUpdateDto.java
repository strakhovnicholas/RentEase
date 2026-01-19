package ru.practicum.shareit.server.item.dto;

public record ItemUpdateDto(
        String name,
        String description,
        Boolean available) {
}

