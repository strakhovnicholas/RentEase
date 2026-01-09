package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

public interface ItemWithBookings {
    Item getItem();
    LocalDateTime getLastBooking();
    LocalDateTime getNextBooking();
}
