package ru.practicum.shareit.server.comment.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
        Long id,
        String text,
        LocalDateTime created,
        Long itemId,
        Long authorId,
        String authorName
) { }
