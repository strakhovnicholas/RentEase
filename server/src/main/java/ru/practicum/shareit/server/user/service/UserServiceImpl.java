package ru.practicum.shareit.server.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.server.user.exception.UserNotFoundException;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.repository.UserRepository;


import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final UserMapper userMapper;

    @Override
    public Collection<ItemResponseDto> getAllUserItems(Long userId) {
        return this.itemService.getUserItems(userId);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return this.userMapper.toDto(user);
    }

    @Override
    public UserResponseDto createUser(UserCreateDto userRequestDto) {
        this.userRepository.findByEmail(userRequestDto.email())
                .ifPresent(existing -> {
                    throw new UserAlreadyExistsException("User already exists with email: " + userRequestDto.email());
                });

        User user = userMapper.toEntity(userRequestDto);
        return this.userMapper.toDto(this.userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserUpdateDto userRequestDto) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional.ofNullable(userRequestDto.email())
                .ifPresent(newEmail -> {
                    if (!newEmail.equals(user.getEmail())) {
                        userRepository.findByEmail(newEmail)
                                .ifPresent(existing -> {
                                    throw new UserAlreadyExistsException("User already exists with email: " + newEmail);
                                });
                    }
                });

        this.userMapper.updateUserFromDto(userRequestDto, user);
        User savedUser = userRepository.save(user);
        return this.userMapper.toDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        this.userRepository.deleteById(userId);
    }
}

