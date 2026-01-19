package ru.practicum.shareit.server.booking.exception;

import ru.practicum.shareit.server.exception.common.AccessDeniedException;

public class BookingAccessDeniedException extends AccessDeniedException {
    public BookingAccessDeniedException(Long userId, Long bookingId) {
        super(String.format("User %d is not the owner of item in booking %d", userId, bookingId));
    }
}
