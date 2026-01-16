package ru.practicum.shareit.server.user.service;


import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {
    Collection<ItemResponseDto> getAllUserItems(Long userId);

    UserResponseDto getUserById(Long userId);

    UserResponseDto createUser(UserCreateDto userRequestDto);

    UserResponseDto updateUser(Long userId, UserUpdateDto userRequestDto);

    void deleteUser(Long userId);
}

