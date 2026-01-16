package ru.practicum.shareit.server.item.dto;

import ru.practicum.shareit.server.item.model.Item;

import java.time.LocalDateTime;

public interface ItemWithBookings {
    Item getItem();

    LocalDateTime getLastBooking();

    LocalDateTime getNextBooking();
}
