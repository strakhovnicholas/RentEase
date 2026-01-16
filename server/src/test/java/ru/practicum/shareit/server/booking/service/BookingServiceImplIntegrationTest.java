package ru.practicum.shareit.server.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.enums.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // Используем усечение до микросекунд для согласованности с БД
        now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void getBookingsByBooker_WithAllStates_ShouldReturnAllBookings() {
        createBooking(now.minusDays(10), now.minusDays(5), BookingStatus.WAITING);
        createBooking(now.minusDays(3), now.plusDays(2), BookingStatus.APPROVED);
        createBooking(now.plusDays(5), now.plusDays(10), BookingStatus.REJECTED);

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(
                booker.getId(), "ALL", 0, 10);

        assertThat(result).hasSize(3);

        // Используем сравнение с допуском
        assertThat(result.get(0).bookingStartDate())
                .isCloseTo(now.plusDays(5), within(1, ChronoUnit.MICROS));
        assertThat(result.get(1).bookingStartDate())
                .isCloseTo(now.minusDays(3), within(1, ChronoUnit.MICROS));
        assertThat(result.get(2).bookingStartDate())
                .isCloseTo(now.minusDays(10), within(1, ChronoUnit.MICROS));
    }

    @Test
    void getBookingsByBooker_WithStateFilter_ShouldFilterCorrectly() {
        createBooking(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        createBooking(now.plusDays(5), now.plusDays(6), BookingStatus.REJECTED);

        assertThat(bookingService.getBookingsByBooker(booker.getId(), "WAITING", 0, 10))
                .hasSize(1)
                .extracting("status").containsOnly(BookingStatus.WAITING);

        assertThat(bookingService.getBookingsByBooker(booker.getId(), "REJECTED", 0, 10))
                .hasSize(1)
                .extracting("status").containsOnly(BookingStatus.REJECTED);
    }

    @Test
    void getBookingsByBooker_WithPagination_ShouldWorkCorrectly() {
        for (int i = 1; i <= 15; i++) {
            createBooking(now.plusDays(i), now.plusDays(i + 1), BookingStatus.APPROVED);
        }

        List<BookingResponseDto> page1 = bookingService.getBookingsByBooker(
                booker.getId(), "ALL", 0, 10);
        List<BookingResponseDto> page2 = bookingService.getBookingsByBooker(
                booker.getId(), "ALL", 10, 10);

        assertThat(page1).hasSize(10);
        assertThat(page2).hasSize(5);

        List<Long> page1Ids = page1.stream().map(BookingResponseDto::id).toList();
        List<Long> page2Ids = page2.stream().map(BookingResponseDto::id).toList();
        assertThat(page1Ids).doesNotContainAnyElementsOf(page2Ids);
    }

    @Test
    void getBookingsByBooker_WhenUserHasNoBookings_ShouldReturnEmptyList() {
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(
                otherUser.getId(), "ALL", 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getBookingsByOwner_ShouldReturnBookingsForOwnersItems() {
        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2 = itemRepository.save(item2);

        createBooking(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        createBookingForItem(item2, now.plusDays(3), now.plusDays(4), BookingStatus.APPROVED);

        User otherOwner = new User();
        otherOwner.setName("Other Owner");
        otherOwner.setEmail("otherowner@example.com");
        otherOwner = userRepository.save(otherOwner);

        Item otherItem = new Item();
        otherItem.setName("Other Item");
        otherItem.setDescription("Other Desc");
        otherItem.setAvailable(true);
        otherItem.setOwner(otherOwner);
        otherItem = itemRepository.save(otherItem);

        createBookingForItem(otherItem, now.plusDays(5), now.plusDays(6), BookingStatus.WAITING);

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(
                owner.getId(), "ALL", 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("item.ownerId").containsOnly(owner.getId());
    }

    @Test
    void getBookingsByOwner_WithStateFilter_ShouldFilterCorrectly() {
        createBooking(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        createBooking(now.plusDays(5), now.plusDays(6), BookingStatus.REJECTED);

        assertThat(bookingService.getBookingsByOwner(owner.getId(), "WAITING", 0, 10))
                .hasSize(1)
                .extracting("status").containsOnly(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByOwner_WithTimeBasedStates_ShouldFilterCorrectly() {
        createBooking(now.minusDays(10), now.minusDays(5), BookingStatus.APPROVED);
        createBooking(now.minusDays(2), now.plusDays(1), BookingStatus.APPROVED);
        createBooking(now.plusDays(5), now.plusDays(10), BookingStatus.WAITING);

        assertThat(bookingService.getBookingsByOwner(owner.getId(), "PAST", 0, 10))
                .hasSize(1)
                .allMatch(b -> b.bookingEndDate().isBefore(now));

        assertThat(bookingService.getBookingsByOwner(owner.getId(), "CURRENT", 0, 10))
                .hasSize(1)
                .allMatch(b -> b.bookingStartDate().isBefore(now) && b.bookingEndDate().isAfter(now));

        assertThat(bookingService.getBookingsByOwner(owner.getId(), "FUTURE", 0, 10))
                .hasSize(1)
                .allMatch(b -> b.bookingStartDate().isAfter(now));
    }

    @Test
    void getBookingsByOwner_WhenOwnerHasNoItems_ShouldReturnEmptyList() {
        User userWithoutItems = new User();
        userWithoutItems.setName("No Items");
        userWithoutItems.setEmail("noitems@example.com");
        userWithoutItems = userRepository.save(userWithoutItems);

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(
                userWithoutItems.getId(), "ALL", 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getBookingsByOwner_WithInvalidUserId_ShouldThrowException() {
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bookingService.getBookingsByOwner(999L, "ALL", 0, 10)
        )).isInstanceOf(RuntimeException.class);
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setBookingStartDate(start.truncatedTo(ChronoUnit.MICROS));
        booking.setBookingEndDate(end.truncatedTo(ChronoUnit.MICROS));
        booking.setStatus(status);
        booking.setCreated(now);
        return bookingRepository.save(booking);
    }

    private Booking createBookingForItem(Item item, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setBookingStartDate(start.truncatedTo(ChronoUnit.MICROS));
        booking.setBookingEndDate(end.truncatedTo(ChronoUnit.MICROS));
        booking.setStatus(status);
        booking.setCreated(now);
        return bookingRepository.save(booking);
    }
}