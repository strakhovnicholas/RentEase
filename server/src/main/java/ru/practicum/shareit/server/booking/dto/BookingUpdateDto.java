package ru.practicum.shareit.server.booking.dto;

import ru.practicum.shareit.server.booking.enums.BookingStatus;

public record BookingUpdateDto(
        Long id,
        BookingStatus status
) {
}
