package ru.practicum.shareit.gateway.core.item.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.shareit.gateway.core.item.dto.item.CreateItemDto;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemForRequestDto;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemResponseDto;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemUpdateDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createItemDto_WithValidData_ShouldCreateSuccessfully() {
        CreateItemDto dto = CreateItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertEquals("Item Name", dto.name());
        assertEquals("Item Description", dto.description());
        assertTrue(dto.available());
    }

    @Test
    void createItemDto_WithNullName_ShouldFailValidation() {
        CreateItemDto dto = CreateItemDto.builder()
                .name(null)
                .description("Description")
                .available(true)
                .build();

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void createItemDto_WithEmptyName_ShouldFailValidation() {
        CreateItemDto dto = CreateItemDto.builder()
                .name("")
                .description("Description")
                .available(true)
                .build();

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void createItemDto_WithNullAvailable_ShouldFailValidation() {
        CreateItemDto dto = CreateItemDto.builder()
                .name("Name")
                .description("Description")
                .available(null)
                .build();

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void itemForRequestDto_ShouldCreateSuccessfully() {
        ItemForRequestDto dto = ItemForRequestDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Description")
                .available(true)
                .ownerId(2L)
                .build();

        assertEquals(1L, dto.id());
        assertEquals("Item Name", dto.name());
        assertEquals("Description", dto.description());
        assertTrue(dto.available());
        assertEquals(2L, dto.ownerId());
    }

    @Test
    void itemForRequestDto_WithNullFields_ShouldCreateSuccessfully() {
        ItemForRequestDto dto = ItemForRequestDto.builder()
                .id(null)
                .name(null)
                .description(null)
                .available(null)
                .ownerId(null)
                .build();

        assertNull(dto.id());
        assertNull(dto.name());
        assertNull(dto.description());
        assertNull(dto.available());
        assertNull(dto.ownerId());
    }

    @Test
    void itemResponseDto_ShouldCreateSuccessfully() {
        ItemResponseDto dto = ItemResponseDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Description")
                .available(true)
                .ownerId(2L)
                .comments(null)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(3L)
                .build();

        assertEquals(1L, dto.id());
        assertEquals("Item Name", dto.name());
        assertEquals("Description", dto.description());
        assertTrue(dto.available());
        assertEquals(2L, dto.ownerId());
        assertNotNull(dto.comments());
        assertTrue(dto.comments().isEmpty());
        assertNull(dto.lastBooking());
        assertNull(dto.nextBooking());
        assertEquals(3L, dto.requestId());
    }

    @Test
    void itemResponseDto_WithAllFieldsNull_ShouldCreateSuccessfully() {
        ItemResponseDto dto = ItemResponseDto.builder()
                .id(null)
                .name(null)
                .description(null)
                .available(null)
                .ownerId(null)
                .comments(null)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(null)
                .build();

        assertNull(dto.id());
        assertNull(dto.name());
        assertNull(dto.description());
        assertNull(dto.available());
        assertNotNull(dto.comments());
        assertTrue(dto.comments().isEmpty());;
        assertNull(dto.lastBooking());
        assertNull(dto.nextBooking());
        assertNull(dto.requestId());
    }

    @Test
    void itemUpdateDto_ShouldCreateSuccessfully() {
        ItemUpdateDto dto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        assertEquals("Updated Name", dto.name());
        assertEquals("Updated Description", dto.description());
        assertFalse(dto.available());
    }

    @Test
    void itemUpdateDto_WithPartialUpdate_ShouldCreateSuccessfully() {
        ItemUpdateDto dto = ItemUpdateDto.builder()
                .name("Only Name Updated")
                .description(null)
                .available(null)
                .build();

        assertEquals("Only Name Updated", dto.name());
        assertNull(dto.description());
        assertNull(dto.available());
    }

    @Test
    void itemUpdateDto_WithEmptyUpdate_ShouldCreateSuccessfully() {
        ItemUpdateDto dto = ItemUpdateDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        assertNull(dto.name());
        assertNull(dto.description());
        assertNull(dto.available());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForCreateItemDto() {
        CreateItemDto dto1 = CreateItemDto.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();

        CreateItemDto dto2 = CreateItemDto.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForItemUpdateDto() {
        ItemUpdateDto dto1 = ItemUpdateDto.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();

        ItemUpdateDto dto2 = ItemUpdateDto.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}