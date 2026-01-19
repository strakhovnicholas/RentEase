package ru.practicum.shareit.gateway.core.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserUpdateDtoJsonTest {
    @Autowired
    private JacksonTester<UserUpdateDto> json;

    @Test
    void testSerialize() throws IOException {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        String result = json.write(dto).getJson();

        assertThat(result).contains("\"name\":\"Updated Name\"");
        assertThat(result).contains("\"email\":\"updated@example.com\"");
    }

    @Test
    void testDeserialize() throws IOException {
        String jsonContent = "{" +
                "\"name\":\"Updated Name\"," +
                "\"email\":\"updated@example.com\"" +
                "}";

        UserUpdateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo("Updated Name");
        assertThat(dto.email()).isEqualTo("updated@example.com");
    }

    @Test
    void testDeserialize_PartialUpdate() throws IOException {
        String jsonContent = "{" +
                "\"name\":\"Only Name Updated\"" +
                "}";

        UserUpdateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo("Only Name Updated");
        assertThat(dto.email()).isNull();
    }

    @Test
    void testDeserialize_EmptyUpdate() throws IOException {
        String jsonContent = "{}";

        UserUpdateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isNull();
        assertThat(dto.email()).isNull();
    }
}