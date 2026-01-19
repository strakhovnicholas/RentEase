package ru.practicum.shareit.gateway.core.booking.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.gateway.core.booking.BookingClient;
import ru.practicum.shareit.gateway.core.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.gateway.special.utils.HttpHeaders;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestBody @Valid BookItemRequestDto requestDto,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId) {

        log.info("Gateway: POST /bookings - Creating booking for user: {}, item: {}",
                userId, requestDto.itemId());

        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PositiveOrZero @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId) {

        log.info("Gateway: PATCH /bookings/{} - User {} {} booking",
                bookingId, userId, approved ? "approving" : "rejecting");

        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @PositiveOrZero @PathVariable Long bookingId,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId) {

        log.info("Gateway: GET /bookings/{} - Getting booking for user: {}",
                bookingId, userId);

        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
            @NotBlank @NotNull @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("Gateway: GET /bookings/owner - User: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);

        return bookingClient.getOwnerBookings(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("Gateway: GET /bookings - User: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);

        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Object> cancelBooking(
            @PositiveOrZero @PathVariable Long bookingId,
            @PositiveOrZero @RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId) {

        log.info("Gateway: PATCH /bookings/{}/cancel - User {} cancelling booking",
                bookingId, userId);

        return bookingClient.cancelBooking(bookingId, userId);
    }
}