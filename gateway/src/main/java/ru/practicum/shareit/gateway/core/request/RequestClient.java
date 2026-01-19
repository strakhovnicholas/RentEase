package ru.practicum.shareit.gateway.core.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.core.request.dto.CreateRequestDto;
import ru.practicum.shareit.gateway.special.client.BaseClient;
import ru.practicum.shareit.gateway.special.utils.PropertyPlaceholders;
import ru.practicum.shareit.gateway.special.utils.RestTemplateFactory;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value(PropertyPlaceholders.SERVER_URL) String serverUrl,
                         RestTemplateBuilder builder) {
        super(RestTemplateFactory.createRestTemplate(serverUrl, API_PREFIX, builder));
    }

    public ResponseEntity<Object> getUserOwnRequests(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getOtherUsersRequests(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequestById(Long requestId) {
        return get("/" + requestId);
    }

    public ResponseEntity<Object> createRequest(Long userId, CreateRequestDto requestDto) {
        return post("", userId, requestDto);
    }
}