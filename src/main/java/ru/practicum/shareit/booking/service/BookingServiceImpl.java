package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingAccessDeniedException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.util.BookingServiceUtils;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingCreateDto bookingCreateDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id=%d not found", bookerId)));

        Item item = itemRepository.findById(bookingCreateDto.itemId())
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Item with id=%d not found", bookingCreateDto.itemId())));

        BookingServiceUtils.validateBookingRules(booker.getId(), item, bookingCreateDto);

        Booking booking = this.bookingMapper.toEntity(bookingCreateDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setCreated(LocalDateTime.now());

        this.bookingRepository.save(booking);

        log.info("Booking created with id={} for user={}", booking.getId(), bookerId);
        return this.bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId, Long bookerId) {
        Booking booking = bookingRepository.findByIdWithItem(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Booking with id=%d not found", bookingId)));

        BookingServiceUtils.validateCancellationRules(booking, bookerId);

        booking.setStatus(BookingStatus.CANCELLED);
        log.info("Booking id={} cancelled by user id={}", bookingId, bookerId);

        Item item = booking.getItem();
        item.setAvailable(true);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto manageBooking(Long requesterId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Booking with id=%d not found", bookingId)));

        BookingServiceUtils.checkOwnerAndRequestor(booking, requesterId);

        Item item = booking.getItem();
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            item.setAvailable(false);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            item.setAvailable(true);
        }

        log.info("User {} {} booking {}", item.getOwner(),
                approved ? "approving" : "rejecting", bookingId);

        return bookingMapper.toDto(booking);

    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponseDto getBooking(Long requesterId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Booking with id=%d not found", bookingId)));
        validateBookingAccessOrThrow(booking, requesterId);

        return bookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingsByBooker(Long userId, String state, int from, int size) {
        return getBookings(userId, state, from, size,
                bookingRepository::findByBookerIdAndState,
                "booker");
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long userId, String state, int from, int size) {
        return getBookings(userId, state, from, size,
                bookingRepository::findByOwnerIdAndState,
                "owner");
    }

    @FunctionalInterface
    private interface BookingQuery {
        Page<Booking> find(Long userId, String state, LocalDateTime now, Pageable pageable);
    }

    private List<BookingResponseDto> getBookings(Long userId, String state, int from, int size,
                                                 BookingQuery query, String userRole) {
        log.info("Getting bookings where user {} is {}, state: {}", userId, userRole, state);

        if (!userRepository.existsById(userId)) {
            log.warn("User with ID {} not found", userId);
            throw new UserNotFoundException("User not found");
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("bookingStartDate").descending());

        log.debug("Calling repository with page={}, size={}", page, size);

        Page<Booking> pageResult = query.find(
                userId,
                state.toUpperCase(),
                LocalDateTime.now(),
                pageable);

        log.info("Found {} bookings out of {} total for user {} as {}",
                pageResult.getNumberOfElements(), pageResult.getTotalElements(), userId, userRole);

        return pageResult.getContent().stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateBookingAccessOrThrow(Booking booking, Long requesterId) {
        boolean isOwner = booking.getItem().getOwner().getId().equals(requesterId);
        boolean isBooker = booking.getBooker().getId().equals(requesterId);

        if (!isOwner && !isBooker) {
            throw new BookingAccessDeniedException(requesterId, booking.getId());
        }
    }
}
