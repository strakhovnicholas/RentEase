package ru.practicum.shareit.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record BookingCreateDto(
        Long itemId,
        @JsonProperty("start")
        LocalDateTime bookingStartDate,
        @JsonProperty("end")
        LocalDateTime bookingEndDate
) { }