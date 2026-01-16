package ru.practicum.shareit.gateway.core.request.controller;

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
import ru.practicum.shareit.gateway.core.request.RequestClient;
import ru.practicum.shareit.gateway.core.request.dto.CreateRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    private RequestController requestController;

    @Mock
    private RequestClient requestClient;

    private CreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        requestController = new RequestController(requestClient);

        createRequestDto = CreateRequestDto.builder()
                .description("Need a drill for home renovation")
                .build();
    }

    @Test
    void findUserOwnRequests_ShouldCallClientWithCorrectParameters() {
        Long requestorId = 1L;
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user requests");
        when(requestClient.getUserOwnRequests(requestorId, 0, 20))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findUserOwnRequests(requestorId, pageable);

        verify(requestClient).getUserOwnRequests(requestorId, 0, 20);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findUserOwnRequests_WithCustomPageable_ShouldCalculateFromCorrectly() {
        Long requestorId = 1L;
        Pageable pageable = PageRequest.of(2, 10, Sort.by("createdAt").descending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user requests");
        when(requestClient.getUserOwnRequests(requestorId, 20, 10))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findUserOwnRequests(requestorId, pageable);

        verify(requestClient).getUserOwnRequests(requestorId, 20, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findOtherUsersRequests_ShouldCallClientWithCorrectParameters() {
        Long requestorId = 1L;
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("other users requests");
        when(requestClient.getOtherUsersRequests(requestorId, 0, 20))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findOtherUsersRequests(requestorId, pageable);

        verify(requestClient).getOtherUsersRequests(requestorId, 0, 20);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findOtherUsersRequests_WithDifferentPagination_ShouldPassCorrectValues() {
        Long requestorId = 2L;
        Pageable pageable = PageRequest.of(1, 5);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("other users requests");
        when(requestClient.getOtherUsersRequests(requestorId, 5, 5))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findOtherUsersRequests(requestorId, pageable);

        verify(requestClient).getOtherUsersRequests(requestorId, 5, 5);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findRequestById_ShouldCallClientWithCorrectId() {
        Long requestId = 123L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request details");
        when(requestClient.getRequestById(requestId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findRequestById(requestId);

        verify(requestClient).getRequestById(requestId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findRequestById_WithZeroId_ShouldPassZeroToClient() {
        Long requestId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request details");
        when(requestClient.getRequestById(requestId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findRequestById(requestId);

        verify(requestClient).getRequestById(requestId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createRequest_ShouldCallClientWithCorrectParameters() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse =
                ResponseEntity.status(HttpStatus.CREATED).body("request created");

        when(requestClient.createRequest(userId, createRequestDto))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.createRequest(userId, createRequestDto);

        verify(requestClient).createRequest(userId, createRequestDto);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
    }

    @Test
    void createRequest_WithArgumentCaptor_ShouldPassCorrectDto() {
        Long userId = 1L;
        String description = "Looking for a ladder for painting";

        CreateRequestDto requestDto = CreateRequestDto.builder()
                .description(description)
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(requestClient.createRequest(eq(userId), any(CreateRequestDto.class)))
                .thenReturn(expectedResponse);

        requestController.createRequest(userId, requestDto);

        ArgumentCaptor<CreateRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(CreateRequestDto.class);
        verify(requestClient).createRequest(eq(userId), dtoCaptor.capture());

        CreateRequestDto capturedDto = dtoCaptor.getValue();
        assertEquals(description, capturedDto.description());
    }

    @Test
    void createRequest_WithZeroUserId_ShouldPassZeroToClient() {
        Long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(requestClient.createRequest(userId, createRequestDto))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.createRequest(userId, createRequestDto);

        verify(requestClient).createRequest(userId, createRequestDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void controllerMethods_ShouldHandleErrorResponses() {
        Long requestId = 999L;
        ResponseEntity<Object> errorResponse =
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");

        when(requestClient.getRequestById(requestId)).thenReturn(errorResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findRequestById(requestId);

        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertEquals("Request not found", actualResponse.getBody());
    }

    @Test
    void findUserOwnRequests_WithDefaultSorting_ShouldUseCreatedAtDescending() {
        Long requestorId = 1L;

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(requestClient.getUserOwnRequests(requestorId, 0, 20))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findUserOwnRequests(requestorId, pageable);

        verify(requestClient).getUserOwnRequests(requestorId, 0, 20);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findOtherUsersRequests_WhenNoOtherUsersRequests_ShouldReturnEmptyList() {
        Long requestorId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("[]");
        when(requestClient.getOtherUsersRequests(requestorId, 0, 20))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                requestController.findOtherUsersRequests(requestorId, pageable);

        verify(requestClient).getOtherUsersRequests(requestorId, 0, 20);
        assertEquals(expectedResponse, actualResponse);
    }
}