package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    ItemResponseDto getItemById(Long itemId);

    ItemResponseDto createItem(ItemRequestDto requestDto, Long ownerId);

    ItemResponseDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId);

    Collection<ItemResponseDto> getAllItems();

    Collection<ItemResponseDto> searchItems(String query, Long ownerId);

    Collection<ItemResponseDto> getUserItems(Long userId);

    void deleteItem(Long itemId);
}

