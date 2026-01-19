package ru.practicum.shareit.gateway.core.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.core.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.gateway.special.client.BaseClient;
import ru.practicum.shareit.gateway.special.utils.PropertyPlaceholders;
import ru.practicum.shareit.gateway.special.utils.RestTemplateFactory;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value(PropertyPlaceholders.SERVER_URL) String serverUrl,
                         RestTemplateBuilder builder) {
        super(RestTemplateFactory.createRestTemplate(serverUrl, API_PREFIX, builder));
    }

    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> approveBooking(long bookingId, Boolean approved, long userId) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBooking(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getOwnerBookings(long userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getUserBookings(long userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> cancelBooking(long bookingId, long userId) {
        return patch("/" + bookingId + "/cancel", userId, null, null);
    }
}