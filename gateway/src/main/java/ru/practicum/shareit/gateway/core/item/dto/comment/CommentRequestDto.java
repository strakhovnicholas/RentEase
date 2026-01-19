package ru.practicum.shareit.gateway.core.item.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentRequestDto(
        @NotBlank(message = "Comment text cannot be empty")
        @Size(min = 1, max = 1000, message = "Comment text must be between 1 and 1000 characters")
        String text
) {
}