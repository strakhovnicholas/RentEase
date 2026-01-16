package ru.practicum.shareit.gateway.core.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoJsonTest {

    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    void testDeserialize_WithInstant() throws IOException {
        String jsonContent = "{\"id\":1,\"description\":\"Test\",\"created\":\"2024-01-15T10:00:00Z\"}";

        RequestDto dto = json.parseObject(jsonContent);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.description()).isEqualTo("Test");
        assertThat(dto.created()).isEqualTo(Instant.parse("2024-01-15T10:00:00Z"));
    }

    @Test
    void testSerialize_WithInstant() throws IOException {
        Instant created = Instant.parse("2024-01-15T10:00:00Z");
        RequestDto dto = RequestDto.builder()
                .id(1L)
                .description("Test")
                .requesterId(2L)
                .created(created)
                .items(null)
                .build();

        String result = json.write(dto).getJson();

        assertThat(result).contains("\"created\":\"2024-01-15T10:00:00Z\"");
    }
}