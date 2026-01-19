package ru.practicum.shareit.gateway.core.request.dto;


import lombok.Builder;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemForRequestDto;

import java.time.Instant;
import java.util.List;

@Builder
public record RequestDto(
        Long id,
        String description,
        Long requesterId,
        Instant created,
        List<ItemForRequestDto> items
) {
    public RequestDto {
        items = items != null ? List.copyOf(items) : List.of();
    }
}
