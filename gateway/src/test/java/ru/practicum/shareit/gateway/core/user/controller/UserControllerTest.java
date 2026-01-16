package ru.practicum.shareit.gateway.core.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.gateway.core.user.UserClient;
import ru.practicum.shareit.gateway.core.user.dto.UserCreateDto;
import ru.practicum.shareit.gateway.core.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private UserController userController;

    @Mock
    private UserClient userClient;

    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userController = new UserController(userClient);

        userCreateDto = UserCreateDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .build();
    }

    @Test
    void getAllUserItems_ShouldCallClientWithCorrectParameters() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user items");
        when(userClient.getAllUserItems(userId, 0, 20))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.getAllUserItems(userId, pageable);

        verify(userClient).getAllUserItems(userId, 0, 20);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAllUserItems_WithCustomPageable_ShouldCalculateFromCorrectly() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(3, 5);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user items");
        when(userClient.getAllUserItems(userId, 15, 5))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.getAllUserItems(userId, pageable);

        verify(userClient).getAllUserItems(userId, 15, 5);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAllUserItems_WithDefaultSorting_ShouldUseIdDescending() {
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.getAllUserItems(userId, 0, 20))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.getAllUserItems(userId, pageable);

        verify(userClient).getAllUserItems(userId, 0, 20);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserById_ShouldCallClientWithCorrectId() {
        Long userId = 123L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user details");
        when(userClient.getUserById(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.getUserById(userId);

        verify(userClient).getUserById(userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserById_WithZeroId_ShouldPassZeroToClient() {
        Long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user details");
        when(userClient.getUserById(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.getUserById(userId);

        verify(userClient).getUserById(userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createUser_ShouldCallClientWithCorrectDto() {
        ResponseEntity<Object> expectedResponse =
                ResponseEntity.status(HttpStatus.CREATED).body("user created");

        when(userClient.createUser(userCreateDto))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.createUser(userCreateDto);

        verify(userClient).createUser(userCreateDto);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
    }

    @Test
    void createUser_WithArgumentCaptor_ShouldPassCorrectDto() {
        String name = "Alice Smith";
        String email = "alice.smith@example.com";

        UserCreateDto createDto = UserCreateDto.builder()
                .name(name)
                .email(email)
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.createUser(any(UserCreateDto.class)))
                .thenReturn(expectedResponse);

        userController.createUser(createDto);

        ArgumentCaptor<UserCreateDto> dtoCaptor =
                ArgumentCaptor.forClass(UserCreateDto.class);

        verify(userClient).createUser(dtoCaptor.capture());

        UserCreateDto capturedDto = dtoCaptor.getValue();
        assertEquals(name, capturedDto.name());
        assertEquals(email, capturedDto.email());
    }

    @Test
    void updateUser_ShouldCallClientWithCorrectParameters() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user updated");

        when(userClient.updateUser(userId, userUpdateDto))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.updateUser(userId, userUpdateDto);

        verify(userClient).updateUser(userId, userUpdateDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateUser_WithPartialUpdate_ShouldPassCorrectDto() {
        Long userId = 1L;

        UserUpdateDto partialUpdate = UserUpdateDto.builder()
                .name("Only Name Updated")
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated");
        when(userClient.updateUser(userId, partialUpdate))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.updateUser(userId, partialUpdate);

        verify(userClient).updateUser(userId, partialUpdate);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateUser_WithZeroUserId_ShouldPassZeroToClient() {
        Long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.updateUser(userId, userUpdateDto))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.updateUser(userId, userUpdateDto);

        verify(userClient).updateUser(userId, userUpdateDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void deleteUser_ShouldCallClientWithCorrectId() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse =
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        when(userClient.deleteUser(userId))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.deleteUser(userId);

        verify(userClient).deleteUser(userId);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(HttpStatus.NO_CONTENT, actualResponse.getStatusCode());
    }

    @Test
    void deleteUser_WithZeroId_ShouldPassZeroToClient() {
        Long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.deleteUser(userId))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                userController.deleteUser(userId);

        verify(userClient).deleteUser(userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void controllerMethods_ShouldHandleErrorResponses() {
        Long userId = 999L;
        ResponseEntity<Object> errorResponse =
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        when(userClient.getUserById(userId)).thenReturn(errorResponse);

        ResponseEntity<Object> actualResponse =
                userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertEquals("User not found", actualResponse.getBody());
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldReturnConflict() {
        ResponseEntity<Object> conflictResponse =
                ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");

        when(userClient.createUser(userCreateDto))
                .thenReturn(conflictResponse);

        ResponseEntity<Object> actualResponse =
                userController.createUser(userCreateDto);

        assertEquals(HttpStatus.CONFLICT, actualResponse.getStatusCode());
        assertEquals("Email already exists", actualResponse.getBody());
    }

    @Test
    void updateUser_WithInvalidData_ShouldReturnBadRequest() {
        Long userId = 1L;
        ResponseEntity<Object> badRequestResponse =
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format");

        when(userClient.updateUser(userId, userUpdateDto))
                .thenReturn(badRequestResponse);

        ResponseEntity<Object> actualResponse =
                userController.updateUser(userId, userUpdateDto);

        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertEquals("Invalid email format", actualResponse.getBody());
    }

    @Test
    void getAllUserItems_WhenUserHasNoItems_ShouldReturnEmptyList() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        ResponseEntity<Object> emptyResponse = ResponseEntity.ok().body("[]");
        when(userClient.getAllUserItems(userId, 0, 20))
                .thenReturn(emptyResponse);

        ResponseEntity<Object> actualResponse =
                userController.getAllUserItems(userId, pageable);

        verify(userClient).getAllUserItems(userId, 0, 20);
        assertEquals(emptyResponse, actualResponse);
    }
}