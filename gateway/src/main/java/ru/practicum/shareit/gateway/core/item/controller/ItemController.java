package ru.practicum.shareit.gateway.core.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.gateway.core.item.ItemClient;
import ru.practicum.shareit.gateway.core.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.gateway.core.item.dto.item.CreateItemDto;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemUpdateDto;
import ru.practicum.shareit.gateway.special.utils.HttpHeaders;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PositiveOrZero @PathVariable Long itemId,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long ownerId) {

        log.info("Gateway: GET /items/{} - Getting item for user: {}", itemId, ownerId);
        return itemClient.getItemById(itemId, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(
            @RequestBody ItemUpdateDto updateDto,
            @PositiveOrZero @PathVariable Long itemId,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId) {

        log.info("Gateway: PATCH /items/{} - Updating item by user: {}", itemId, userId);
        return itemClient.updateItem(itemId, updateDto, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long ownerId,
            @RequestBody @Valid CreateItemDto itemRequestDto) {

        log.info("Gateway: POST /items - Creating item for user: {}", ownerId);
        return itemClient.createItem(ownerId, itemRequestDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createItemComment(
            @RequestBody @Valid CommentRequestDto request,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
            @PositiveOrZero @PathVariable Long itemId) {

        log.info("Gateway: POST /items/{}/comment - Adding comment by user: {}", itemId, userId);
        return itemClient.createComment(itemId, userId, request);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByQuery(
            @RequestParam(required = false, defaultValue = "") String text,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long ownerId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        log.info("Gateway: GET /items/search?text='{}' by user: {}", text, ownerId);

        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        return itemClient.searchItems(text, ownerId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long ownerId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        log.info("Gateway: GET /items - Getting all items for user: {}, page: {}, size: {}",
                ownerId, pageable.getPageNumber(), pageable.getPageSize());

        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        return itemClient.getUserItems(ownerId, from, size);
    }
}