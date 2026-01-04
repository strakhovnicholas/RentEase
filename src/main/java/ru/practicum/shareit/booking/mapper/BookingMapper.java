package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {
    Booking toEntity(BookingCreateDto bookingCreateDto);

    @Mapping(target = "bookingStartDate", source = "bookingStartDate")
    @Mapping(target = "bookingEndDate", source = "bookingEndDate")
    BookingResponseDto toDto(Booking booking);
}
