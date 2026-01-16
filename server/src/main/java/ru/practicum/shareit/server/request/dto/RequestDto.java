package ru.practicum.shareit.server.request.dto;

import ru.practicum.shareit.server.item.dto.ItemForRequestDto;

import java.time.Instant;
import java.util.List;

public record RequestDto(
        Long id,
        String description,
        Long requesterId,
        Instant created,
        List<ItemForRequestDto> items
) {
}
