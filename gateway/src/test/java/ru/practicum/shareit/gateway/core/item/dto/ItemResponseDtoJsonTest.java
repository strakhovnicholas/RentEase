package ru.practicum.shareit.gateway.core.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseDtoJsonTest {

    @Autowired
    private JacksonTester<ItemResponseDto> json;

    @Test
    void testSerialize_WithAllFields() throws IOException {
        LocalDateTime lastBooking = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime nextBooking = LocalDateTime.of(2024, 1, 20, 14, 0);

        ItemResponseDto dto = ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill for construction")
                .available(true)
                .ownerId(2L)
                .comments(Collections.emptyList())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .requestId(3L)
                .build();

        JsonContent<ItemResponseDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Powerful drill for construction");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo("2024-01-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo("2024-01-20T14:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
    }

    @Test
    void testSerialize_WithNullDates() throws IOException {
        ItemResponseDto dto = ItemResponseDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(2L)
                .comments(null)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(null)
                .build();

        JsonContent<ItemResponseDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isNull();
    }

    @Test
    void testDeserialize_WithDates() throws IOException {
        String jsonContent = "{" +
                "\"id\":1," +
                "\"name\":\"Drill\"," +
                "\"description\":\"Powerful drill\"," +
                "\"available\":true," +
                "\"ownerId\":2," +
                "\"comments\":[]," +
                "\"lastBooking\":\"2024-01-15T10:00:00\"," +
                "\"nextBooking\":\"2024-01-20T14:00:00\"," +
                "\"requestId\":3" +
                "}";

        ItemResponseDto dto = json.parseObject(jsonContent);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Drill");
        assertThat(dto.description()).isEqualTo("Powerful drill");
        assertThat(dto.available()).isTrue();
        assertThat(dto.ownerId()).isEqualTo(2L);
        assertThat(dto.lastBooking()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
        assertThat(dto.nextBooking()).isEqualTo(LocalDateTime.of(2024, 1, 20, 14, 0));
        assertThat(dto.requestId()).isEqualTo(3L);
    }
}