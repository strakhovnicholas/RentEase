package ru.practicum.shareit.gateway.core.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.core.user.UserClient;
import ru.practicum.shareit.gateway.core.user.dto.UserCreateDto;
import ru.practicum.shareit.gateway.core.user.dto.UserUpdateDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}/items")
    public ResponseEntity<Object> getAllUserItems(
            @PositiveOrZero @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {

        log.info("Gateway: GET /users/{}/items - Getting all items for user: {}, page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        return userClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(
            @PositiveOrZero @PathVariable Long userId) {

        log.info("Gateway: GET /users/{} - Getting user by ID", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(
            @Valid @RequestBody UserCreateDto userRequestDto) {

        log.info("Gateway: POST /users - Creating new user");
        return userClient.createUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PositiveOrZero @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {

        log.info("Gateway: PATCH /users/{} - Updating user", userId);
        return userClient.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(
            @PositiveOrZero @PathVariable Long userId) {

        log.info("Gateway: DELETE /users/{} - Deleting user", userId);
        return userClient.deleteUser(userId);
    }
}