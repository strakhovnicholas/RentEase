package ru.practicum.shareit.server.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.CreateRequestDto;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @GetMapping()
    public ResponseEntity<List<RequestDto>> findUserOwnRequests(
            @PositiveOrZero @RequestHeader(value = RequestController.USER_ID_HEADER) Long requestorId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        List<RequestDto> requests = requestService.findUserOwnRequests(requestorId, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> findOtherUsersRequests(
            @PositiveOrZero @RequestHeader(value = RequestController.USER_ID_HEADER) Long requestorId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        List<RequestDto> requests = requestService.findOtherUsersRequests(requestorId, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<RequestDto> findRequestById(
            @PositiveOrZero @PathVariable("requestId") Long requestId) {
        RequestDto request = requestService.findRequestById(requestId);
        return ResponseEntity.ok(request);
    }

    @PostMapping
    public ResponseEntity<RequestDto> createRequest(
            @PositiveOrZero @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody @Valid CreateRequestDto requestDto) {

        RequestDto created = requestService.createRequest(userId, requestDto.description());
        return ResponseEntity.status(201).body(created);
    }
}
