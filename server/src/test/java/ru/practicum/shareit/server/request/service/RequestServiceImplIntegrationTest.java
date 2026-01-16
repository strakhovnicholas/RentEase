package ru.practicum.shareit.server.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.model.Request;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Long userId;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser = userRepository.save(testUser);
        userId = testUser.getId();
    }

    @Test
    void createRequest_ShouldCreateAndSaveRequest_WhenUserExists() {
        String description = "Need a drill for repairs";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertNotNull(createdRequest);
        assertNotNull(createdRequest.id());
        assertEquals(description, createdRequest.description());
        assertEquals(userId, createdRequest.requesterId());
        assertNotNull(createdRequest.created());

        // Используем сравнение с допуском для Instant
        assertTrue(createdRequest.created().isBefore(Instant.now().plusSeconds(1)));

        Optional<Request> savedRequest = requestRepository.findById(createdRequest.id());
        assertTrue(savedRequest.isPresent());
        assertEquals(description, savedRequest.get().getDescription());
        assertEquals(userId, savedRequest.get().getRequester().getId());
    }

    @Test
    void createRequest_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long nonExistentUserId = 999L;
        String description = "Need a drill";

        assertThrows(UserNotFoundException.class, () -> {
            requestService.createRequest(nonExistentUserId, description);
        });

        assertEquals(0, requestRepository.count());
    }

    @Test
    void createRequest_ShouldSetCorrectTimestamp() {
        String description = "Need a hammer drill";
        Instant beforeCreation = Instant.now().minusSeconds(1);

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertNotNull(createdRequest.created());
        assertTrue(createdRequest.created().isAfter(beforeCreation));
        assertTrue(createdRequest.created().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    void createRequest_ShouldReturnRequestWithAllFieldsFilled() {
        String description = "Need a grinding machine";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        // Используем AssertJ для лучшей читаемости
        assertThat(createdRequest.id()).isNotNull();
        assertThat(createdRequest.description()).isEqualTo(description);
        assertThat(createdRequest.requesterId()).isEqualTo(userId);
        assertThat(createdRequest.created()).isNotNull();

        // Проверяем items отдельно, так как он может быть null
        assertThat(createdRequest.items()).isNull();
    }

    @Test
    void createRequest_ShouldHandleEmptyDescription() {
        String emptyDescription = "";

        RequestDto createdRequest = requestService.createRequest(userId, emptyDescription);

        assertEquals(emptyDescription, createdRequest.description());
    }

    @Test
    void createRequest_ShouldHandleMultipleRequestsFromSameUser() {
        String description1 = "First request";
        String description2 = "Second request";

        RequestDto request1 = requestService.createRequest(userId, description1);
        RequestDto request2 = requestService.createRequest(userId, description2);

        assertNotEquals(request1.id(), request2.id());
        assertEquals(description1, request1.description());
        assertEquals(description2, request2.description());
        assertEquals(userId, request1.requesterId());
        assertEquals(userId, request2.requesterId());

        assertEquals(2, requestRepository.count());
        assertTrue(requestRepository.findById(request1.id()).isPresent());
        assertTrue(requestRepository.findById(request2.id()).isPresent());
    }

    @Test
    void createRequest_ShouldIncrementIdCorrectly() {
        String description1 = "Request 1";
        String description2 = "Request 2";

        RequestDto request1 = requestService.createRequest(userId, description1);
        RequestDto request2 = requestService.createRequest(userId, description2);

        // Используем AssertJ для более читаемого сообщения об ошибке
        assertThat(request2.id()).isEqualTo(request1.id() + 1);
    }

    @Test
    void createRequest_ShouldCreateRequestWithItemsListInitiallyEmpty() {
        String description = "Need equipment";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertNull(createdRequest.items());
    }

    @Test
    void createRequest_ShouldPersistCorrectRequesterReference() {
        String description = "Need tools";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        Optional<Request> savedRequest = requestRepository.findById(createdRequest.id());
        assertTrue(savedRequest.isPresent());
        assertEquals(testUser.getId(), savedRequest.get().getRequester().getId());
        assertEquals(testUser.getName(), savedRequest.get().getRequester().getName());
    }

    @Test
    void createRequest_ShouldHandleLongDescriptions() {
        String longDescription = "A".repeat(100);

        RequestDto createdRequest = requestService.createRequest(userId, longDescription);

        assertEquals(longDescription, createdRequest.description());
        assertEquals(longDescription.length(), createdRequest.description().length());
    }

    @Test
    void createRequest_ShouldWorkWithSpecialCharactersInDescription() {
        String description = "Need tool with 100% efficiency! @#$%^&*()";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertEquals(description, createdRequest.description());
    }

    @Test
    void createRequest_ShouldHaveConsistentTimestamps() {
        String description = "Need a tool";

        Instant beforeCreation = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        RequestDto createdRequest = requestService.createRequest(userId, description);
        Instant afterCreation = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        assertThat(createdRequest.created())
                .isBetween(beforeCreation, afterCreation);
    }

    @Test
    void createRequest_ShouldReturnTimestampWithReasonablePrecision() {
        String description = "Test request";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        // Проверяем что разница между текущим временем и временем создания не слишком велика
        long timeDifference = Math.abs(Instant.now().toEpochMilli() - createdRequest.created().toEpochMilli());
        assertTrue(timeDifference < 2000,
                "Timestamp should be recent (difference: " + timeDifference + "ms)");
    }
}