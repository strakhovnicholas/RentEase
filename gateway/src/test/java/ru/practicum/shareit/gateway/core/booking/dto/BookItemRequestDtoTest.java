package ru.practicum.shareit.gateway.core.booking.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookItemRequestDtoTest {

    private Validator validator;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        now = LocalDateTime.now();
    }

    @Test
    void create_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        long itemId = 1L;
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        // Act
        BookItemRequestDto dto = BookItemRequestDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals(itemId, dto.itemId());
        assertEquals(start, dto.start());
        assertEquals(end, dto.end());
    }

    @Test
    void create_WithPastStart_ShouldFailValidation() {
        // Arrange
        LocalDateTime pastStart = now.minusDays(1);

        // Act
        BookItemRequestDto dto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(pastStart)
                .end(now.plusDays(1))
                .build();

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void create_WithPresentStart_ShouldBeValid() {
        // Arrange - создаем дату, которая гарантированно является "present or future"
        LocalDateTime futureStart = now.plusSeconds(1);

        // Act
        BookItemRequestDto dto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(futureStart)
                .end(futureStart.plusDays(1))
                .build();

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_WithPastEnd_ShouldFailValidation() {
        // Act
        BookItemRequestDto dto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(now.plusDays(1))
                .end(now.minusDays(1))
                .build();

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        BookItemRequestDto dto1 = BookItemRequestDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        BookItemRequestDto dto2 = BookItemRequestDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}