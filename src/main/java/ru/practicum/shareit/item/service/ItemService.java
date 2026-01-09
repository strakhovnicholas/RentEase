package ru.practicum.shareit.item.service;


import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import java.util.Collection;

public interface ItemService {
    ItemResponseDto getItemById(Long itemId,Long ownerId);

    ItemResponseDto createItem(ItemRequestDto requestDto, Long ownerId);

    ItemResponseDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId);

    Collection<ItemResponseDto> searchItems(String query, Long ownerId);

    Collection<ItemResponseDto> getUserItems(Long userId);

    void deleteItem(Long itemId);

    Collection<CommentDto> getItemComments(Long itemId);

    CommentDto createItemComment(Long itemId, Long userId, CommentRequestDto text);
}

