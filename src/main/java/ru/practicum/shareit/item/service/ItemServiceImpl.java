package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemResponseDto getItemById(Long itemId) {
        Item item = this.itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        return this.itemMapper.toDto(item);
    }

    @Override
    public ItemResponseDto createItem(ItemRequestDto requestDto, Long ownerId) {
        if (ownerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Sharer-User-Id header is missing");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = this.itemMapper.toEntity(requestDto);
        item.setOwner(owner);
        this.itemRepository.save(item);
        return this.itemMapper.toDto(item);
    }

    public ItemResponseDto updateItem(Long itemId, ItemUpdateDto dto, Long ownerId) {
        if (ownerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Sharer-User-Id header is missing");
        }

        Item item = this.itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new UserNotFoundException("User not found");
        }

        this.itemMapper.updateItemFromDto(dto, item);
        this.itemRepository.save(item);

        return this.itemMapper.toDto(item);
    }

    @Override
    public List<ItemResponseDto> getAllItems() {
        return this.itemRepository
                .findAll()
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String query, Long ownerId) {
        if (!userRepository.findById(ownerId).isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        return this.itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
                .stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .filter(Item::getAvailable)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemResponseDto> getUserItems(Long userId) {
        return this.itemRepository
                .findAll()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        this.itemRepository.deleteById(itemId);
    }
}

