package ru.practicum.shareit.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.shareit.server.booking.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponseDto(
        Long id,
        BookingStatus status,
        @JsonProperty("start")
        LocalDateTime bookingStartDate,
        @JsonProperty("end")
        LocalDateTime bookingEndDate,
        ItemShortDto item,
        UserShortDto booker
) {
    public record ItemShortDto(Long id, String name, @JsonProperty("ownerId") @JsonInclude(JsonInclude
            .Include.ALWAYS) Long ownerId) {}
    public record UserShortDto(Long id, String name) {}
}
