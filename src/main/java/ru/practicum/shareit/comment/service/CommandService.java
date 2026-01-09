package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;
import java.util.Map;

public interface CommandService {
    List<CommentDto> getCommentsForItem(Long itemId);
    Map<Long, List<CommentDto>> getCommentsForItems(List<Long> itemIds);
}
