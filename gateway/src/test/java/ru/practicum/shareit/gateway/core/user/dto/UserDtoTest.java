package ru.practicum.shareit.gateway.core.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userCreateDto_WithValidData_ShouldCreateSuccessfully() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("John Doe")
                .password("password123")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertEquals("John Doe", dto.name());
        assertEquals("password123", dto.password());
        assertEquals("john.doe@example.com", dto.email());
    }

    @Test
    void userCreateDto_WithNullEmail_ShouldFailValidation() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("John Doe")
                .password("password123")
                .email(null)
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void userCreateDto_WithEmptyEmail_ShouldFailValidation() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("John Doe")
                .password("password123")
                .email("")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void userCreateDto_WithInvalidEmail_ShouldFailValidation() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("John Doe")
                .password("password123")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void userCreateDto_WithNullNameAndPassword_ShouldBeValid() {
        UserCreateDto dto = UserCreateDto.builder()
                .name(null)
                .password(null)
                .email("valid@example.com")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertNull(dto.name());
        assertNull(dto.password());
        assertEquals("valid@example.com", dto.email());
    }

    @Test
    void userUpdateDto_WithAllFields_ShouldCreateSuccessfully() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        assertEquals("Updated Name", dto.name());
        assertEquals("updated@example.com", dto.email());
    }

    @Test
    void userUpdateDto_WithPartialUpdate_ShouldCreateSuccessfully() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Only Name Updated")
                .email(null)
                .build();

        assertEquals("Only Name Updated", dto.name());
        assertNull(dto.email());
    }

    @Test
    void userUpdateDto_WithEmptyUpdate_ShouldCreateSuccessfully() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name(null)
                .email(null)
                .build();

        assertNull(dto.name());
        assertNull(dto.email());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForUserCreateDto() {
        UserCreateDto dto1 = UserCreateDto.builder()
                .name("John")
                .password("pass")
                .email("john@example.com")
                .build();

        UserCreateDto dto2 = UserCreateDto.builder()
                .name("John")
                .password("pass")
                .email("john@example.com")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForUserUpdateDto() {
        UserUpdateDto dto1 = UserUpdateDto.builder()
                .name("John")
                .email("john@example.com")
                .build();

        UserUpdateDto dto2 = UserUpdateDto.builder()
                .name("John")
                .email("john@example.com")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}