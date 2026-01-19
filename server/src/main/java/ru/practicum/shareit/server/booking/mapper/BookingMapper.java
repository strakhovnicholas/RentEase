package ru.practicum.shareit.server.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    Booking toEntity(BookingCreateDto bookingCreateDto);

    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "bookingStartDate", source = "bookingStartDate")
    @Mapping(target = "bookingEndDate", source = "bookingEndDate")
    @Mapping(target = "item.ownerId", source = "item.owner.id")
    BookingResponseDto toDto(Booking booking);
}