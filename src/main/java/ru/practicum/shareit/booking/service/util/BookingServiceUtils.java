package ru.practicum.shareit.booking.service.util;

import jakarta.validation.ValidationException;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingAccessDeniedException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Objects;

public class BookingServiceUtils {
    public static void validateBookingRules(Long bookerId, Item item, BookingCreateDto dto) {
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("You cannot book your own item");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }
    }

    public static void validateCancellationRules(Booking booking, Long bookerId) {
        if (!Objects.equals(booking.getBooker().getId(), bookerId)) {
            throw new BookingAccessDeniedException(bookerId, booking.getId());
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ValidationException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.APPROVED &&
                booking.getBookingEndDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot cancel completed booking");
        }

        LocalDateTime now = LocalDateTime.now();
        if (booking.getBookingStartDate().isBefore(now.plusHours(24))) {
            throw new ValidationException(
                    "Cannot cancel booking less than 24 hours before start");
        }
    }

    public static void checkOwnerAndRequestor(Booking booking, Long requesterId) {
        if (!booking.getItem().getOwner().getId().equals(requesterId)) {
            throw new BookingAccessDeniedException(requesterId, booking.getId());
        }
    }
}
