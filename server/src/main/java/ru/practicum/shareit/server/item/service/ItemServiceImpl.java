package ru.practicum.shareit.server.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.enums.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.comment.service.CommentService;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.CommentRequestDto;
import ru.practicum.shareit.server.comment.model.Comment;
import ru.practicum.shareit.server.comment.repository.CommentRepository;
import ru.practicum.shareit.server.item.dto.CreateItemDto;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import ru.practicum.shareit.server.item.exceptions.ItemAccessDeniedException;
import ru.practicum.shareit.server.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.server.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.request.exception.RequestNotFoundException;
import ru.practicum.shareit.server.request.model.Request;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentService commentService;
    private final RequestRepository requestRepository;


    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Long itemId,Long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        List<CommentDto> comments = commentService.getCommentsForItem(itemId);

        boolean isOwner = item.getOwner() != null &&
                item.getOwner().getId().equals(ownerId);

        LocalDateTime lastBookingDate = null;
        LocalDateTime nextBookingDate = null;

        if (isOwner) {
            LocalDateTime now = LocalDateTime.now();

            lastBookingDate = bookingRepository
                    .findLastBookingEndDate(itemId, BookingStatus.APPROVED, now)
                    .orElse(null);

            nextBookingDate = bookingRepository
                    .findNextBookingStartDate(itemId, BookingStatus.APPROVED, now)
                    .orElse(null);
        }

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .comments(comments)
                .lastBooking(lastBookingDate)
                .nextBooking(nextBookingDate)
                .build();
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(CreateItemDto createItemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = this.itemMapper.toEntity(createItemDto);
        item.setOwner(owner);

        if (createItemDto.requestId() != null) {
            Optional<Request> requestOpt = requestRepository.findRequestById(createItemDto.requestId());
            if (requestOpt.isPresent()) {
                item.setRequest(requestOpt.get());
            } else {
                throw new RequestNotFoundException("Request not found: " + createItemDto.requestId());
            }
        }

        this.itemRepository.save(item);

        return this.itemMapper.toDto(item);
    }

    @Transactional
    public ItemResponseDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId) {
        Item item = this.itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        validateItemOwnership(item, ownerId);

        this.itemMapper.updateItemFromDto(dto, item);

        return this.itemMapper.toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItems(String query, Long ownerId) {
        log.debug("Searching items: query='{}', user={}", query, ownerId);

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        Collection<Item> searchedItems = itemRepository.searchAvailableItems(query.toLowerCase());

        if (searchedItems.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = searchedItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<CommentDto>> commentsByItemId = commentService.getCommentsForItems(itemIds);

        return searchedItems.stream()
                .map(item -> {
                    Long itemId = item.getId();
                    ItemResponseDto baseDto = itemMapper.toDto(item);

                    List<CommentDto> comments = commentsByItemId.getOrDefault(itemId, List.of());

                    return ItemResponseDto.builder()
                            .id(baseDto.id())
                            .name(baseDto.name())
                            .description(baseDto.description())
                            .available(baseDto.available())
                            .ownerId(baseDto.ownerId())
                            .comments(comments)
                            .lastBooking(null)
                            .nextBooking(null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemResponseDto> getUserItems(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        List<Object[]> results = itemRepository.findItemsWithBookingsByOwnerId(userId)
                .stream().toList();

        if (results.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = results.stream()
                .map(row -> ((Item) row[0]).getId())
                .collect(Collectors.toList());

        Map<Long, List<CommentDto>> commentsByItem = commentService.getCommentsForItems(itemIds);

        return results.stream()
                .map(row -> convertToDto(row, commentsByItem))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        this.itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CommentDto> getItemComments(Long itemId) {
        Collection<Comment> itemComments = this.commentRepository.findByItemIdOrderByCreatedDesc(itemId);

        return itemComments.stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .created(comment.getCreated())
                        .itemId(itemId)
                        .authorId(comment.getAuthor().getId())
                        .authorName(comment.getAuthor().getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createItemComment(Long itemId, Long userId, CommentRequestDto request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        validateUserCanComment(itemId, userId);

        String text = request.getText();

        Comment comment = Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        comment = commentRepository.save(comment);

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .itemId(itemId)
                .authorId(userId)
                .authorName(author.getName())
                .build();
    }

    private void validateItemOwnership(Item item, Long userId) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        User owner = item.getOwner();
        if (owner == null) {
            throw new IllegalStateException(
                    String.format("Item %d has no owner assigned", item.getId())
            );
        }

        if (!owner.getId().equals(userId)) {
            throw new ItemAccessDeniedException(
                    String.format("User %d is not the owner of item %d", userId, item.getId())
            );
        }
    }

    private void validateUserCanComment(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndStatus(
                itemId, userId, BookingStatus.APPROVED).stream().toList();

        if (bookings.isEmpty()) {
            throw new ValidationException(
                    "User can only comment on items they have booked and approved");
        }

        Booking lastBooking = bookings.stream()
                .max(Comparator.comparing(Booking::getBookingEndDate))
                .orElseThrow(() -> new ValidationException("No bookings found"));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime toleranceTime = now.plusSeconds(5);

        if (lastBooking.getBookingEndDate().isAfter(toleranceTime)) {
            throw new ValidationException(
                    "Cannot comment on active or future booking. Booking ends at: " +
                            lastBooking.getBookingEndDate());
        }

        if (lastBooking.getBookingStartDate().isAfter(now)) {
            throw new ValidationException(
                    "Cannot comment on booking that hasn't started yet");
        }
    }

    private ItemResponseDto convertToDto(Object[] row,
                                         Map<Long, List<CommentDto>> commentsByItem) {
        Item item = (Item) row[0];
        LocalDateTime lastBooking = (LocalDateTime) row[1];
        LocalDateTime nextBooking = (LocalDateTime) row[2];
        Long itemId = item.getId();

        return ItemResponseDto.builder()
                .id(itemId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .comments(commentsByItem.getOrDefault(itemId, List.of()))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }
}

