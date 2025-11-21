package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    @ResponseBody
    public ItemResponseDto getItemById(@PathVariable("itemId") Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseBody
    public ItemResponseDto updateItemById(
            @RequestBody ItemUpdateDto updateDto,
            @PathVariable("itemId") Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemService.updateItem(itemId, updateDto, userId);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
            @RequestBody @Valid ItemRequestDto itemRequestDto) {

        ItemResponseDto response = itemService.createItem(itemRequestDto, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/search")
    @ResponseBody
    public Collection<ItemResponseDto> searchItemsByQuery(@RequestParam String text,
                                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemService.searchItems(text, ownerId);
    }

    @GetMapping
    @ResponseBody
    public Collection<ItemResponseDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        return itemService.getUserItems(ownerId);
    }
}
