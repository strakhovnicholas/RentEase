package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.enums.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.comment.model.Comment;
import ru.practicum.shareit.server.comment.repository.CommentRepository;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private User otherUser;
    private Item item;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking currentBooking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
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

        otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setBookingStartDate(now.minusDays(3));
        pastBooking.setBookingEndDate(now.minusDays(1));
        pastBooking.setCreated(now.minusDays(4));
        bookingRepository.save(pastBooking);

        currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setBookingStartDate(now.minusDays(1));
        currentBooking.setBookingEndDate(now.plusDays(1));
        currentBooking.setCreated(now.minusDays(2));
        bookingRepository.save(currentBooking);

        futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setBookingStartDate(now.plusDays(2));
        futureBooking.setBookingEndDate(now.plusDays(4));
        futureBooking.setCreated(now.minusDays(1));
        bookingRepository.save(futureBooking);

        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        rejectedBooking.setBookingStartDate(now.minusDays(10));
        rejectedBooking.setBookingEndDate(now.minusDays(8));
        rejectedBooking.setCreated(now.minusDays(11));
        bookingRepository.save(rejectedBooking);

        comment = new Comment();
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(now.minusHours(1));
        commentRepository.save(comment);
    }

    @Test
    void getItemById_WhenOwnerRequests_ShouldReturnWithBookingsAndComments() {
        ItemResponseDto result = itemService.getItemById(item.getId(), owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(item.getId());
        assertThat(result.name()).isEqualTo(item.getName());
        assertThat(result.description()).isEqualTo(item.getDescription());
        assertThat(result.available()).isEqualTo(item.getAvailable());
        assertThat(result.ownerId()).isEqualTo(owner.getId());

        assertThat(result.lastBooking()).isNotNull();
        assertThat(result.nextBooking()).isNotNull();

        assertThat(result.lastBooking()).isEqualTo(pastBooking.getBookingEndDate());
        assertThat(result.nextBooking()).isEqualTo(futureBooking.getBookingStartDate());

        assertThat(result.comments()).hasSize(1);
        assertThat(result.comments().stream().toList().getFirst().text()).isEqualTo("Great item!");
        assertThat(result.comments().stream().toList().getFirst().authorName()).isEqualTo(booker.getName());
        assertThat(result.comments().stream().toList().getFirst().itemId()).isEqualTo(item.getId());
    }

    @Test
    void getItemById_WhenNonOwnerRequests_ShouldReturnWithoutBookingsButWithComments() {
        ItemResponseDto result = itemService.getItemById(item.getId(), otherUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(item.getId());
        assertThat(result.name()).isEqualTo(item.getName());
        assertThat(result.description()).isEqualTo(item.getDescription());
        assertThat(result.available()).isEqualTo(item.getAvailable());
        assertThat(result.ownerId()).isEqualTo(owner.getId());

        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();

        assertThat(result.comments()).hasSize(1);
        assertThat(result.comments().stream().toList().getFirst().text()).isEqualTo("Great item!");
    }

    @Test
    void getItemById_WhenBookerRequests_ShouldReturnWithoutBookings() {
        ItemResponseDto result = itemService.getItemById(item.getId(), booker.getId());

        assertThat(result).isNotNull();
        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
        assertThat(result.comments()).hasSize(1);
    }

    @Test
    void getItemById_WhenItemHasNoBookings_ShouldReturnNullBookings() {
        Item itemWithoutBookings = new Item();
        itemWithoutBookings.setName("Item Without Bookings");
        itemWithoutBookings.setDescription("Description");
        itemWithoutBookings.setAvailable(true);
        itemWithoutBookings.setOwner(owner);
        itemWithoutBookings = itemRepository.save(itemWithoutBookings);

        ItemResponseDto result = itemService.getItemById(itemWithoutBookings.getId(), owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
        assertThat(result.comments()).isEmpty();
    }

    @Test
    void getItemById_WhenItemHasOnlyPastBookings_ShouldReturnOnlyLastBooking() {
        bookingRepository.delete(currentBooking);
        bookingRepository.delete(futureBooking);

        ItemResponseDto result = itemService.getItemById(item.getId(), owner.getId());

        assertThat(result.lastBooking()).isEqualTo(pastBooking.getBookingEndDate());
        assertThat(result.nextBooking()).isNull();
    }

    @Test
    void getItemById_WhenItemHasOnlyFutureBookings_ShouldReturnOnlyNextBooking() {
        bookingRepository.delete(pastBooking);
        bookingRepository.delete(currentBooking);

        ItemResponseDto result = itemService.getItemById(item.getId(), owner.getId());

        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isEqualTo(futureBooking.getBookingStartDate());
    }

    @Test
    void getItemById_WhenItemHasMultipleComments_ShouldReturnAllComments() {
        Comment comment2 = new Comment();
        comment2.setText("Excellent!");
        comment2.setItem(item);
        comment2.setAuthor(otherUser);
        comment2.setCreated(LocalDateTime.now().minusHours(2));
        commentRepository.save(comment2);

        Comment comment3 = new Comment();
        comment3.setText("Very useful");
        comment3.setItem(item);
        comment3.setAuthor(booker);
        comment3.setCreated(LocalDateTime.now().minusHours(3));
        commentRepository.save(comment3);

        ItemResponseDto result = itemService.getItemById(item.getId(), owner.getId());

        assertThat(result.comments()).hasSize(3);
        assertThat(result.comments().stream().toList().getFirst().text()).isEqualTo("Great item!");
        assertThat(result.comments().stream().toList().get(1).text()).isEqualTo("Excellent!");
        assertThat(result.comments().stream().toList().get(2).text()).isEqualTo("Very useful");
    }

    @Test
    void getItemById_WhenItemNotFound_ShouldThrowException() {
        assertThatThrownBy(() -> itemService.getItemById(999L, owner.getId()))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found");
    }

    @Test
    void getItemById_WhenItemOwnerIsNull_ShouldHandleGracefully() {
        Item itemWithoutOwner = new Item();
        itemWithoutOwner.setName("Item Without Owner");
        itemWithoutOwner.setDescription("Description");
        itemWithoutOwner.setAvailable(true);
        itemWithoutOwner.setOwner(null);
        itemWithoutOwner = itemRepository.save(itemWithoutOwner);

        ItemResponseDto result = itemService.getItemById(itemWithoutOwner.getId(), owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.ownerId()).isNull();
        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
    }

    @Test
    void getItemById_WithRejectedBookingStatus_ShouldNotAffectLastNextBookings() {
        bookingRepository.delete(pastBooking);
        bookingRepository.delete(currentBooking);
        bookingRepository.delete(futureBooking);

        ItemResponseDto result = itemService.getItemById(item.getId(), owner.getId());

        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
    }

    @Test
    void getItemById_WhenUserIsNull_ShouldWorkWithoutNPE() {
        ItemResponseDto result = itemService.getItemById(item.getId(), null);

        assertThat(result).isNotNull();
        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
    }
}