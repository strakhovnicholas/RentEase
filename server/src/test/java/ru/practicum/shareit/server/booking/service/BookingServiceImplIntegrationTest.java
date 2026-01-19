package ru.practicum.shareit.server.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.server.AllMappersTestConfig;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.enums.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.entity.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({BookingServiceImpl.class, AllMappersTestConfig.class})
class BookingServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        entityManager.persist(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);

        entityManager.flush();
    }

    @Test
    void getBookingsByBooker_WithAllStates_ShouldReturnAllBookings() {
        createBooking(now.minusDays(10), now.minusDays(5), BookingStatus.WAITING);
        createBooking(now.minusDays(3), now.plusDays(2), BookingStatus.APPROVED);
        createBooking(now.plusDays(5), now.plusDays(10), BookingStatus.REJECTED);

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(
                booker.getId(), "ALL", 0, 10);

        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting("status")
                .containsExactly(BookingStatus.REJECTED, BookingStatus.APPROVED, BookingStatus.WAITING);
    }

    @Test
    void getBookingsByBooker_WithStateFilter_ShouldFilterCorrectly() {
        createBooking(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        createBooking(now.plusDays(5), now.plusDays(6), BookingStatus.REJECTED);

        List<BookingResponseDto> waitingBookings = bookingService.getBookingsByBooker(
                booker.getId(), "WAITING", 0, 10);
        assertThat(waitingBookings)
                .hasSize(1)
                .extracting("status")
                .containsOnly(BookingStatus.WAITING);

        List<BookingResponseDto> rejectedBookings = bookingService.getBookingsByBooker(
                booker.getId(), "REJECTED", 0, 10);
        assertThat(rejectedBookings)
                .hasSize(1)
                .extracting("status")
                .containsOnly(BookingStatus.REJECTED);
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
        entityManager.persist(otherUser);
        entityManager.flush();

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
        entityManager.persist(item2);

        createBooking(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        createBookingForItem(item2, now.plusDays(3), now.plusDays(4), BookingStatus.APPROVED);

        User otherOwner = new User();
        otherOwner.setName("Other Owner");
        otherOwner.setEmail("otherowner@example.com");
        entityManager.persist(otherOwner);

        Item otherItem = new Item();
        otherItem.setName("Other Item");
        otherItem.setDescription("Other Desc");
        otherItem.setAvailable(true);
        otherItem.setOwner(otherOwner);
        entityManager.persist(otherItem);

        createBookingForItem(otherItem, now.plusDays(5), now.plusDays(6), BookingStatus.WAITING);

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(
                owner.getId(), "ALL", 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(dto -> dto.item().ownerId())
                .containsOnly(owner.getId());
    }

    @Test
    void getBookingsByOwner_WithStateFilter_ShouldFilterCorrectly() {
        createBooking(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        createBooking(now.plusDays(5), now.plusDays(6), BookingStatus.REJECTED);

        List<BookingResponseDto> waitingBookings = bookingService.getBookingsByOwner(
                owner.getId(), "WAITING", 0, 10);
        assertThat(waitingBookings)
                .hasSize(1)
                .extracting("status")
                .containsOnly(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByOwner_WithTimeBasedStates_ShouldFilterCorrectly() {
        createBooking(now.minusDays(10), now.minusDays(5), BookingStatus.APPROVED);
        createBooking(now.minusDays(2), now.plusDays(1), BookingStatus.APPROVED);
        createBooking(now.plusDays(5), now.plusDays(10), BookingStatus.WAITING);

        List<BookingResponseDto> pastBookings = bookingService.getBookingsByOwner(
                owner.getId(), "PAST", 0, 10);
        assertThat(pastBookings)
                .hasSize(1)
                .allMatch(b -> b.bookingEndDate().isBefore(now));

        List<BookingResponseDto> currentBookings = bookingService.getBookingsByOwner(
                owner.getId(), "CURRENT", 0, 10);
        assertThat(currentBookings)
                .hasSize(1)
                .allMatch(b -> b.bookingStartDate().isBefore(now) && b.bookingEndDate().isAfter(now));

        List<BookingResponseDto> futureBookings = bookingService.getBookingsByOwner(
                owner.getId(), "FUTURE", 0, 10);
        assertThat(futureBookings)
                .hasSize(1)
                .allMatch(b -> b.bookingStartDate().isAfter(now));
    }

    @Test
    void getBookingsByOwner_WhenOwnerHasNoItems_ShouldReturnEmptyList() {
        User userWithoutItems = new User();
        userWithoutItems.setName("No Items");
        userWithoutItems.setEmail("noitems@example.com");
        entityManager.persist(userWithoutItems);
        entityManager.flush();

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(
                userWithoutItems.getId(), "ALL", 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getBookingsByOwner_WithInvalidUserId_ShouldThrowException() {
        assertThatThrownBy(() -> bookingService.getBookingsByOwner(999L, "ALL", 0, 10))
                .isInstanceOf(RuntimeException.class);
    }

    private void createBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setBookingStartDate(start);
        booking.setBookingEndDate(end);
        booking.setStatus(status);
        booking.setCreated(now);
        entityManager.persist(booking);
        entityManager.flush();
    }

    private void createBookingForItem(Item item, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setBookingStartDate(start);
        booking.setBookingEndDate(end);
        booking.setStatus(status);
        booking.setCreated(now);
        entityManager.persist(booking);
        entityManager.flush();
    }
}