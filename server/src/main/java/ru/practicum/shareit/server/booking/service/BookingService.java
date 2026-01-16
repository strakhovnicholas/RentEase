package ru.practicum.shareit.server.booking.service;

import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingResponseDto cancelBooking(Long bookingId, Long bookerId);

    BookingResponseDto manageBooking(Long itemOwnerId, Long bookingId, Boolean approved);

    BookingResponseDto getBooking(Long requesterId, Long bookingId);

    List<BookingResponseDto> getBookingsByBooker(Long userId, String state, int from, int size);

    List<BookingResponseDto> getBookingsByOwner(Long userId, String state, int from, int size);
}
