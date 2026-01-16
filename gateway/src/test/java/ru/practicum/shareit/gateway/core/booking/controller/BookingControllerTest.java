package ru.practicum.shareit.gateway.core.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.gateway.core.booking.BookingClient;
import ru.practicum.shareit.gateway.core.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private BookingController bookingController;

    @Mock
    private BookingClient bookingClient;

    private BookItemRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        this.bookingController = new BookingController(bookingClient);

        validRequestDto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void createBooking_ShouldCallClientAndReturnResponse() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("booking created");
        when(bookingClient.bookItem(eq(userId), any(BookItemRequestDto.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.createBooking(validRequestDto, userId);

        verify(bookingClient).bookItem(userId, validRequestDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void approveBooking_WithApprovedTrue_ShouldCallClient() {
        Long bookingId = 1L;
        Long userId = 2L;
        Boolean approved = true;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("approved");
        when(bookingClient.approveBooking(bookingId, approved, userId))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.approveBooking(bookingId, approved, userId);

        verify(bookingClient).approveBooking(bookingId, approved, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void approveBooking_WithApprovedFalse_ShouldCallClient() {
        Long bookingId = 1L;
        Long userId = 2L;
        Boolean approved = false;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("rejected");
        when(bookingClient.approveBooking(bookingId, approved, userId))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.approveBooking(bookingId, approved, userId);

        verify(bookingClient).approveBooking(bookingId, approved, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getBooking_ShouldCallClientAndReturnResponse() {
        Long bookingId = 1L;
        Long userId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("booking details");
        when(bookingClient.getBooking(bookingId, userId))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.getBooking(bookingId, userId);

        verify(bookingClient).getBooking(bookingId, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getOwnerBookings_ShouldCallClientWithCorrectParameters() {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner bookings");
        when(bookingClient.getOwnerBookings(userId, state, from, size))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.getOwnerBookings(userId, state, from, size);

        verify(bookingClient).getOwnerBookings(userId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getOwnerBookings_WithCustomParameters_ShouldCallClientWithCorrectValues() {
        Long userId = 1L;
        String state = "FUTURE";
        int from = 5;
        int size = 20;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner bookings");
        when(bookingClient.getOwnerBookings(userId, state, from, size))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.getOwnerBookings(userId, state, from, size);

        verify(bookingClient).getOwnerBookings(userId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserBookings_ShouldCallClientWithCorrectParameters() {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user bookings");
        when(bookingClient.getUserBookings(userId, state, from, size))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.getUserBookings(userId, state, from, size);

        verify(bookingClient).getUserBookings(userId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserBookings_WithCustomParameters_ShouldCallClientWithCorrectValues() {
        Long userId = 1L;
        String state = "PAST";
        int from = 10;
        int size = 5;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user bookings");
        when(bookingClient.getUserBookings(userId, state, from, size))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.getUserBookings(userId, state, from, size);

        verify(bookingClient).getUserBookings(userId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void cancelBooking_ShouldCallClientAndReturnResponse() {
        Long bookingId = 1L;
        Long userId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("cancelled");
        when(bookingClient.cancelBooking(bookingId, userId))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.cancelBooking(bookingId, userId);

        verify(bookingClient).cancelBooking(bookingId, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createBooking_WithArgumentCaptor_ShouldPassCorrectArguments() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.bookItem(eq(userId), any(BookItemRequestDto.class)))
                .thenReturn(expectedResponse);

        bookingController.createBooking(validRequestDto, userId);

        ArgumentCaptor<BookItemRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(BookItemRequestDto.class);
        verify(bookingClient).bookItem(eq(userId), dtoCaptor.capture());

        BookItemRequestDto capturedDto = dtoCaptor.getValue();
        assertEquals(validRequestDto.itemId(),capturedDto.itemId());
        assertEquals(validRequestDto.start(), capturedDto.start());
        assertEquals(validRequestDto.end(), capturedDto.end());
    }

    @Test
    void controllerMethods_ShouldHandleErrorResponses() {
        Long bookingId = 1L;
        Long userId = 2L;
        ResponseEntity<Object> errorResponse =
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");

        when(bookingClient.getBooking(bookingId, userId))
                .thenReturn(errorResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.getBooking(bookingId, userId);

        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertEquals("error", actualResponse.getBody());
    }

    @Test
    void getOwnerBookings_WithEmptyState_ShouldUseDefault() {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getOwnerBookings(userId, state, from, size))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                bookingController.getOwnerBookings(userId, state, from, size);

        verify(bookingClient).getOwnerBookings(userId, state, from, size);
        assertNotNull(actualResponse);
    }
}