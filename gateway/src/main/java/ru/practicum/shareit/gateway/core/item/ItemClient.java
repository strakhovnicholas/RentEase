package ru.practicum.shareit.gateway.core.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.core.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.gateway.core.item.dto.item.CreateItemDto;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemUpdateDto;
import ru.practicum.shareit.gateway.special.client.BaseClient;
import ru.practicum.shareit.gateway.special.utils.PropertyPlaceholders;
import ru.practicum.shareit.gateway.special.utils.RestTemplateFactory;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value(PropertyPlaceholders.SERVER_URL) String serverUrl,
                      RestTemplateBuilder builder) {
        super(RestTemplateFactory.createRestTemplate(serverUrl, API_PREFIX, builder));
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> updateItem(Long itemId, ItemUpdateDto updateDto, Long userId) {
        return patch("/" + itemId, userId, updateDto);
    }

    public ResponseEntity<Object> createItem(Long ownerId, CreateItemDto itemRequestDto) {
        return post("", ownerId, itemRequestDto);
    }

    public ResponseEntity<Object> createComment(Long itemId, Long userId, CommentRequestDto request) {
        return post("/" + itemId + "/comment", userId, request);
    }

    public ResponseEntity<Object> searchItems(String text, Long ownerId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> getUserItems(Long ownerId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", ownerId, parameters);
    }
}