package ru.practicum.shareit.server.item.service;


import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.CommentRequestDto;
import ru.practicum.shareit.server.item.dto.CreateItemDto;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import java.util.Collection;

public interface ItemService {
    ItemResponseDto getItemById(Long itemId,Long ownerId);

    ItemResponseDto createItem(CreateItemDto requestDto, Long ownerId);

    ItemResponseDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId);

    Collection<ItemResponseDto> searchItems(String query, Long ownerId);

    Collection<ItemResponseDto> getUserItems(Long userId);

    void deleteItem(Long itemId);

    Collection<CommentDto> getItemComments(Long itemId);

    CommentDto createItemComment(Long itemId, Long userId, CommentRequestDto text);
}

