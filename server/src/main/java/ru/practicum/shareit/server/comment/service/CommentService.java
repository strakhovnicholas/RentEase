package ru.practicum.shareit.server.comment.service;

import ru.practicum.shareit.server.comment.dto.CommentDto;

import java.util.List;
import java.util.Map;

public interface CommentService {
    List<CommentDto> getCommentsForItem(Long itemId);

    Map<Long, List<CommentDto>> getCommentsForItems(List<Long> itemIds);
}
