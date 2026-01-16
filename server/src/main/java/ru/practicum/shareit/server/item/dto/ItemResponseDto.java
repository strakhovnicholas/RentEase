package ru.practicum.shareit.server.item.dto;

import lombok.Builder;
import ru.practicum.shareit.server.comment.dto.CommentDto;

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

