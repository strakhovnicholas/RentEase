package ru.practicum.shareit.server.user;

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
    public Collection<ItemResponseDto> getAllUserItems(@PathVariable Long userId) {
        return userService.getAllUserItems(userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserCreateDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}

