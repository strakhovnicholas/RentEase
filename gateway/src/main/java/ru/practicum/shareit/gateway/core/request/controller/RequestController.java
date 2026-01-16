package ru.practicum.shareit.gateway.core.request.controller;

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
import ru.practicum.shareit.gateway.core.request.RequestClient;
import ru.practicum.shareit.gateway.core.request.dto.CreateRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> findUserOwnRequests(
            @PositiveOrZero @RequestHeader(USER_ID_HEADER) Long requestorId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        log.info("Gateway: GET /requests - Getting user's own requests for user: {}", requestorId);

        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        return requestClient.getUserOwnRequests(requestorId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findOtherUsersRequests(
            @PositiveOrZero @RequestHeader(USER_ID_HEADER) Long requestorId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        log.info("Gateway: GET /requests/all - Getting other users' requests for user: {}", requestorId);

        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        return requestClient.getOtherUsersRequests(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(
            @PositiveOrZero @PathVariable Long requestId) {

        log.info("Gateway: GET /requests/{} - Getting request by ID", requestId);
        return requestClient.getRequestById(requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @PositiveOrZero @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody @Valid CreateRequestDto requestDto) {

        log.info("Gateway: POST /requests - Creating request for user: {}", userId);
        return requestClient.createRequest(userId, requestDto);
    }
}