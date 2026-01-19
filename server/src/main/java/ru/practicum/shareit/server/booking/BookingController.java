package ru.practicum.shareit.server.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestBody BookingCreateDto requestDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("POST /bookings - Creating booking for user: {}, item: {}",
                userId, requestDto.itemId());

        BookingResponseDto response = bookingService.createBooking(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("PATCH /bookings/{} - User {} {} booking",
                bookingId, userId, approved ? "approving" : "rejecting");

        BookingResponseDto response = bookingService.manageBooking(userId, bookingId, approved);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(
            @PathVariable Long bookingId,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("GET /bookings/{} - Getting booking for user: {}", bookingId, userId);

        BookingResponseDto booking = bookingService.getBooking(userId, bookingId);
        return ResponseEntity.ok(booking);
    }


    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0")  int from,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /bookings - Getting bookings for owner: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);

        List<BookingResponseDto> bookings = bookingService.getBookingsByOwner(userId, state, from, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /bookings - Getting bookings for user: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);

        List<BookingResponseDto> bookings = bookingService.getBookingsByBooker(userId, state, from, size);
        return ResponseEntity.ok(bookings);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable Long bookingId,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("PATCH /bookings/{}/cancel - User {} cancelling booking",
                bookingId, userId);

        BookingResponseDto response = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(response);
    }
}