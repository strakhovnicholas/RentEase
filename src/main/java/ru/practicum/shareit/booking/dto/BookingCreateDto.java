package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingCreateDto(
        @NotNull(message = "Item ID cannot be null")
        Long itemId,

        @NotNull(message = "Start date cannot be null")
        @Future(message = "Start date must be in future")
        @JsonProperty("start")
        LocalDateTime bookingStartDate,

        @NotNull(message = "End date cannot be null")
        @Future(message = "End date must be in future")
        @JsonProperty("end")
        LocalDateTime bookingEndDate
) {

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndAfterStart() {
        if (bookingStartDate == null || bookingEndDate == null) {
            return true;
        }
        return bookingEndDate.isAfter(bookingStartDate);
    }
}
