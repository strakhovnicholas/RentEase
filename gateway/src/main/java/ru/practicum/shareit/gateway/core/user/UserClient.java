package ru.practicum.shareit.gateway.core.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.core.user.dto.UserCreateDto;
import ru.practicum.shareit.gateway.core.user.dto.UserUpdateDto;
import ru.practicum.shareit.gateway.special.client.BaseClient;
import ru.practicum.shareit.gateway.special.utils.PropertyPlaceholders;
import ru.practicum.shareit.gateway.special.utils.RestTemplateFactory;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value(PropertyPlaceholders.SERVER_URL) String serverUrl,
                      RestTemplateBuilder builder) {
        super(RestTemplateFactory.createRestTemplate(serverUrl, API_PREFIX, builder));
    }

    public ResponseEntity<Object> getAllUserItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/" + userId + "/items?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> createUser(UserCreateDto userRequestDto) {
        return post("",null, userRequestDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserUpdateDto userUpdateDto) {
        return patch("/" + userId, userId, userUpdateDto);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId, userId);
    }
}