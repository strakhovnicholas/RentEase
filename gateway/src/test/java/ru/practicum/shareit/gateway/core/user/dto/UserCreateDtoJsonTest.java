package ru.practicum.shareit.gateway.core.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserCreateDtoJsonTest {

    @Autowired
    private JacksonTester<UserCreateDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private Validator validator;

    @BeforeEach
    void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testSerialize() throws IOException {
        UserCreateDto dto = UserCreateDto.builder()
                .name("John Doe")
                .password("secret123")
                .email("john.doe@example.com")
                .build();

        String result = objectMapper.writeValueAsString(dto);

        assertThat(result).contains("\"name\":\"John Doe\"");
        assertThat(result).contains("\"password\":\"secret123\"");
        assertThat(result).contains("\"email\":\"john.doe@example.com\"");
    }

    @Test
    void testDeserialize_WithValidData() throws IOException {
        String jsonContent = "{" +
                "\"name\":\"John Doe\"," +
                "\"password\":\"secret123\"," +
                "\"email\":\"john.doe@example.com\"" +
                "}";

        UserCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo("John Doe");
        assertThat(dto.password()).isEqualTo("secret123");
        assertThat(dto.email()).isEqualTo("john.doe@example.com");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testDeserialize_WithInvalidEmail() throws IOException {
        String jsonContent = "{" +
                "\"name\":\"John Doe\"," +
                "\"password\":\"secret123\"," +
                "\"email\":\"invalid-email\"" +
                "}";

        UserCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.email()).isEqualTo("invalid-email");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("Email should be valid");
    }

    @Test
    void testDeserialize_WithMissingEmail() throws IOException {
        String jsonContent = "{" +
                "\"name\":\"John Doe\"," +
                "\"password\":\"secret123\"" +
                "}";

        UserCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo("John Doe");
        assertThat(dto.password()).isEqualTo("secret123");
        assertThat(dto.email()).isNull();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testDeserialize_WithEmptyEmail() throws IOException {
        String jsonContent = "{" +
                "\"name\":\"John Doe\"," +
                "\"password\":\"secret123\"," +
                "\"email\":\"\"" +
                "}";

        UserCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.email()).isEmpty();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testDeserialize_WithMinimalData() throws IOException {
        String jsonContent = "{" +
                "\"email\":\"valid@example.com\"" +
                "}";

        UserCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isNull();
        assertThat(dto.password()).isNull();
        assertThat(dto.email()).isEqualTo("valid@example.com");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }
}