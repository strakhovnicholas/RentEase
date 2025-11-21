package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Getter
@Setter
public class Booking {
    private Long id;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private List<Item> items;
    private User booker;
    private BookingStatus status;
}
