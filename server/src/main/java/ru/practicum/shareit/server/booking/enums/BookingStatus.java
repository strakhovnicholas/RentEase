package ru.practicum.shareit.server.booking.enums;

import java.util.Arrays;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELLED, CURRENT, PAST, FUTURE;

    public static BookingStatus fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown state: " + value +
                            ". Available: " + Arrays.toString(values())
            );
        }
    }

    public boolean isStatusBased() {
        return this == WAITING || this == REJECTED;
    }

    public boolean isTimeBased() {
        return this == CURRENT || this == PAST || this == FUTURE;
    }
}
