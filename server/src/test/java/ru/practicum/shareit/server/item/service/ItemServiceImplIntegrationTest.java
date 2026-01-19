package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.server.AllMappersTestConfig;
import ru.practicum.shareit.server.booking.enums.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.model.Comment;
import ru.practicum.shareit.server.comment.service.CommentService;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.entity.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

@DataJpaTest
@Import({ItemServiceImpl.class, AllMappersTestConfig.class})
class ItemServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemServiceImpl itemService;

    @MockBean
    private CommentService commentService;

    private User owner;
    private User booker;
    private User otherUser;
    private Item item;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking currentBooking;
    private Comment comment;
    private CommentDto commentDto;
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

        otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        entityManager.persist(otherUser);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);

        pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setBookingStartDate(now.minusDays(3));
        pastBooking.setBookingEndDate(now.minusDays(1));
        pastBooking.setCreated(now.minusDays(4));
        entityManager.persist(pastBooking);

        currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setBookingStartDate(now.minusDays(1));
        currentBooking.setBookingEndDate(now.plusDays(1));
        currentBooking.setCreated(now.minusDays(2));
        entityManager.persist(currentBooking);

        futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setBookingStartDate(now.plusDays(2));
        futureBooking.setBookingEndDate(now.plusDays(4));
        futureBooking.setCreated(now.minusDays(1));
        entityManager.persist(futureBooking);

        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        rejectedBooking.setBookingStartDate(now.minusDays(10));
        rejectedBooking.setBookingEndDate(now.minusDays(8));
        rejectedBooking.setCreated(now.minusDays(11));
        entityManager.persist(rejectedBooking);

        comment = new Comment();
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(now.minusHours(1));
        entityManager.persist(comment);

        entityManager.flush();

        commentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .itemId(item.getId())
                .authorId(booker.getId())
                .authorName(booker.getName())
                .build();

        when(commentService.getCommentsForItem(anyLong()))
                .thenReturn(List.of());
    }

    @Test
    void getItemById_WhenOwnerRequests_ShouldReturnWithBookingsAndComments() {
        when(commentService.getCommentsForItem(item.getId()))
                .thenReturn(List.of(commentDto));

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
        when(commentService.getCommentsForItem(item.getId()))
                .thenReturn(List.of(commentDto));
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

        System.out.println(result);
        assertThat(result).isNotNull();
        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
    }

    @Test
    void getItemById_WhenItemHasNoBookings_ShouldReturnNullBookings() {
        Item itemWithoutBookings = new Item();
        itemWithoutBookings.setName("Item Without Bookings");
        itemWithoutBookings.setDescription("Description");
        itemWithoutBookings.setAvailable(true);
        itemWithoutBookings.setOwner(owner);
        entityManager.persist(itemWithoutBookings);
        entityManager.flush();

        ItemResponseDto result = itemService.getItemById(itemWithoutBookings.getId(), owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
        assertThat(result.comments()).isEmpty();
    }

    @Test
    void getItemById_WhenItemHasOnlyPastBookings_ShouldReturnOnlyLastBooking() {
        entityManager.remove(currentBooking);
        entityManager.remove(futureBooking);
        entityManager.flush();

        ItemResponseDto result = itemService.getItemById(item.getId(), owner.getId());

        assertThat(result.lastBooking()).isEqualTo(pastBooking.getBookingEndDate());
        assertThat(result.nextBooking()).isNull();
    }

    @Test
    void getItemById_WhenItemHasOnlyFutureBookings_ShouldReturnOnlyNextBooking() {
        entityManager.remove(pastBooking);
        entityManager.remove(currentBooking);
        entityManager.flush();

        ItemResponseDto result = itemService.getItemById(item.getId(), owner.getId());

        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isEqualTo(futureBooking.getBookingStartDate());
    }

    @Test
    void getItemById_WhenItemHasMultipleComments_ShouldReturnAllComments() {
        LocalDateTime now = LocalDateTime.now();

        Comment comment2 = new Comment();
        comment2.setText("Excellent!");
        comment2.setItem(item);
        comment2.setAuthor(otherUser);
        comment2.setCreated(now.minusHours(2));
        entityManager.persist(comment2);

        Comment comment3 = new Comment();
        comment3.setText("Very useful");
        comment3.setItem(item);
        comment3.setAuthor(booker);
        comment3.setCreated(now.minusHours(3));
        entityManager.persist(comment3);

        entityManager.flush();

        CommentDto commentDto2 = CommentDto.builder()
                .id(comment2.getId())
                .text(comment2.getText())
                .created(comment2.getCreated())
                .itemId(item.getId())
                .authorId(otherUser.getId())
                .authorName(otherUser.getName())
                .build();

        CommentDto commentDto3 = CommentDto.builder()
                .id(comment3.getId())
                .text(comment3.getText())
                .created(comment3.getCreated())
                .itemId(item.getId())
                .authorId(booker.getId())
                .authorName(booker.getName())
                .build();

        when(commentService.getCommentsForItem(item.getId()))
                .thenReturn(List.of(commentDto, commentDto2, commentDto3));

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
        entityManager.persist(itemWithoutOwner);
        entityManager.flush();

        ItemResponseDto result = itemService.getItemById(itemWithoutOwner.getId(), owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.ownerId()).isNull();
        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
    }

    @Test
    void getItemById_WithRejectedBookingStatus_ShouldNotAffectLastNextBookings() {
        entityManager.remove(pastBooking);
        entityManager.remove(currentBooking);
        entityManager.remove(futureBooking);
        entityManager.flush();

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