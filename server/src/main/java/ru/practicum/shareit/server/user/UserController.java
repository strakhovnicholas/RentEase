package ru.practicum.shareit.server.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/{userId}/items")
    public Collection<ItemResponseDto> getAllUserItems(@PositiveOrZero @PathVariable Long userId) {
        return userService.getAllUserItems(userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PositiveOrZero @PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody UserCreateDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(
            @PositiveOrZero @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PositiveOrZero @PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}

