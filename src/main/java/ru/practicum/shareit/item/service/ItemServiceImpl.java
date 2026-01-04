package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exceptions.ItemAccessDeniedException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        List<CommentDto> commentDto = commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .created(comment.getCreated())
                        .authorId(comment.getAuthor().getId())
                        .build())
                .collect(Collectors.toList());

        LocalDateTime lastBookingDate = bookingRepository
                .findLastBookingEndDate(itemId, BookingStatus.PAST, LocalDateTime.now()).orElse(null);

        LocalDateTime nextBookingDate = bookingRepository
                .findNextBookingStartDate(itemId, BookingStatus.WAITING, LocalDateTime.now()).orElse(null);

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .comments(commentDto)
                .lastBooking(lastBookingDate)
                .nextBooking(nextBookingDate)
                .build();
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(ItemRequestDto itemRequestDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = this.itemMapper.toEntity(itemRequestDto);
        item.setOwner(owner);
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
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("User not found");
        }

        Collection<Item> searchedItemsByNameOrDescription = this.itemRepository.searchAvailableItems(query);

        return searchedItemsByNameOrDescription.stream()
                .map(itemMapper::toDto)
                .toList();


    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemResponseDto> getUserItems(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Collection<Item> ownerItems = this.itemRepository.findByOwnerIdOrderByName(userId);

        return ownerItems.stream()
                .map(this.itemMapper::toDto)
                .toList();
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
}

