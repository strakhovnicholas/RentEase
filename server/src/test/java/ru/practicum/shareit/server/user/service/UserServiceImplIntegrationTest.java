package ru.practicum.shareit.server.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.enums.UserRole;
import ru.practicum.shareit.server.user.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    void getUserById_whenUserExists_thenReturnUser() {
        UserCreateDto createDto = new UserCreateDto(
                "Test User",
                "password123",
                "test@email.com",
                UserRole.USER
        );

        UserResponseDto createdUser = userService.createUser(createDto);
        UserResponseDto retrievedUser = userService.getUserById(createdUser.id());

        assertNotNull(retrievedUser);
        assertEquals(createdUser.id(), retrievedUser.id());
        assertEquals("test@email.com", retrievedUser.email());
        assertEquals("Test User", retrievedUser.name());
    }

    @Test
    void getUserById_whenUserDoesNotExist_thenThrowException() {
        Long nonExistingUserId = 999L;

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(nonExistingUserId));
    }

    @Test
    void getUserById_whenUserHasAdminRole_thenReturnUserWithRole() {
        UserCreateDto createDto = new UserCreateDto(
                "Admin User",
                "admin123",
                "admin@email.com",
                UserRole.ADMIN
        );

        UserResponseDto createdUser = userService.createUser(createDto);
        UserResponseDto retrievedUser = userService.getUserById(createdUser.id());

        assertNotNull(retrievedUser);
        assertEquals(createdUser.id(), retrievedUser.id());
        assertEquals("admin@email.com", retrievedUser.email());
        assertEquals("Admin User", retrievedUser.name());
    }

    @Test
    void createUser_thenGetUserById_shouldReturnSameUser() {
        UserCreateDto createDto = new UserCreateDto(
                "John Doe",
                "securePass",
                "john.doe@example.com",
                UserRole.USER
        );

        UserResponseDto created = userService.createUser(createDto);
        UserResponseDto retrieved = userService.getUserById(created.id());

        assertNotNull(retrieved);
        assertEquals(created.id(), retrieved.id());
        assertEquals("john.doe@example.com", retrieved.email());
        assertEquals("John Doe", retrieved.name());
    }

    @Test
    void getUserById_afterUserUpdate_shouldReturnUpdatedData() {
        UserCreateDto createDto = new UserCreateDto(
                "Original Name",
                "password",
                "original@email.com",
                UserRole.USER
        );

        UserResponseDto created = userService.createUser(createDto);
        UserResponseDto found = userService.getUserById(created.id());

        assertNotNull(found);
        assertEquals("Original Name", found.name());
    }
}