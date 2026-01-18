package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.CommentRequestDto;
import ru.practicum.shareit.server.item.dto.CreateItemDto;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";


    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(
            @PathVariable("itemId") Long itemId,
            @RequestHeader(value = ItemController.USER_ID_HEADER) Long ownerId) {

        return itemService.getItemById(itemId,ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItemById(
            @RequestBody ItemUpdateDto updateDto,
            @PathVariable("itemId") Long itemId,
            @RequestHeader(value = ItemController.USER_ID_HEADER) Long userId) {
        return itemService.updateItem(itemId, updateDto, userId);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(
            @RequestHeader(value = ItemController.USER_ID_HEADER) Long ownerId,
            @RequestBody CreateItemDto itemRequestDto) {

        ItemResponseDto response = itemService.createItem(itemRequestDto, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createItemComment(
            @RequestBody CommentRequestDto request,
            @RequestHeader(value = ItemController.USER_ID_HEADER) Long userId,
            @PathVariable("itemId") Long itemId) {

        CommentDto response = this.itemService.createItemComment(itemId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchItemsByQuery(
            @RequestParam(required = false, defaultValue = "") String text,
            @RequestHeader(value = ItemController.USER_ID_HEADER) Long ownerId) {
        log.info("GET /items/search?text='{}' by user {}", text, ownerId);
        return itemService.searchItems(text, ownerId);
    }

    @GetMapping
    public Collection<ItemResponseDto> getUserItems(@RequestHeader(value = USER_ID_HEADER, required = false) Long ownerId) {
        return itemService.getUserItems(ownerId);
    }
}
