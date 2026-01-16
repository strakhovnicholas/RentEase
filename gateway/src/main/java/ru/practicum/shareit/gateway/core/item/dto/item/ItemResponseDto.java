package ru.practicum.shareit.gateway.core.item.dto.item;

import lombok.Builder;
import ru.practicum.shareit.gateway.core.item.dto.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
public record ItemResponseDto(
        Long id,
        String name,
        String description,
        Boolean available,
        Long ownerId,
        Collection<CommentDto> comments,
        LocalDateTime lastBooking,
        LocalDateTime nextBooking,
        Long requestId) { }

