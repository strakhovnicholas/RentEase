package ru.practicum.shareit.gateway.core.item.dto.comment;

import lombok.Builder;

@Builder
public record CommentRequestDto (
        String text
)
{ }
