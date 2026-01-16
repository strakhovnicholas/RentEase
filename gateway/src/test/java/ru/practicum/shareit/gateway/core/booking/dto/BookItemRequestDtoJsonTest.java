package ru.practicum.shareit.gateway.core.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2024, 1, 15, 10, 0);
        end = LocalDateTime.of(2024, 1, 16, 12, 0);
    }

    @Test
    void testSerialize() throws IOException {
        BookItemRequestDto dto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-16T12:00:00");
    }

    @Test
    void testDeserialize() throws IOException {
        String jsonContent = "{\"itemId\":1,\"start\":\"2024-01-15T10:00:00\",\"end\":\"2024-01-16T12:00:00\"}";

        BookItemRequestDto dto = json.parseObject(jsonContent);

        assertThat(dto.itemId()).isEqualTo(1L);
        assertThat(dto.start()).isEqualTo(start);
        assertThat(dto.end()).isEqualTo(end);
    }

    @Test
    void testDeserialize_WithDifferentDateTimeFormat() throws IOException {
        String jsonContent = "{\"itemId\":1,\"start\":\"2024-01-15T10:00\",\"end\":\"2024-01-16T12:00\"}";

        BookItemRequestDto dto = json.parseObject(jsonContent);

        assertThat(dto.itemId()).isEqualTo(1L);
        assertThat(dto.start()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
        assertThat(dto.end()).isEqualTo(LocalDateTime.of(2024, 1, 16, 12, 0));
    }
}