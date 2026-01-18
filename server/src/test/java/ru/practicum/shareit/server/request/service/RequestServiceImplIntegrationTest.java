package ru.practicum.shareit.server.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.server.AllMappersTestConfig;
import ru.practicum.shareit.server.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.model.Request;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.user.entity.User;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({RequestServiceImpl.class, AllMappersTestConfig.class})
class RequestServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    private User testUser;
    private Long userId;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        entityManager.persist(testUser);
        entityManager.flush();

        userId = testUser.getId();
    }

    @Test
    void createRequest_ShouldCreateAndSaveRequest_WhenUserExists() {
        String description = "Need a drill for repairs";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest.id()).isNotNull();
        assertThat(createdRequest.description()).isEqualTo(description);
        assertThat(createdRequest.requesterId()).isEqualTo(userId);
        assertThat(createdRequest.created()).isNotNull();

        Request savedRequest = entityManager.find(Request.class, createdRequest.id());
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo(description);
        assertThat(savedRequest.getRequester().getId()).isEqualTo(userId);
    }

    @Test
    void createRequest_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long nonExistentUserId = 999L;
        String description = "Need a drill";

        assertThatThrownBy(() -> requestService.createRequest(nonExistentUserId, description))
                .isInstanceOf(UserNotFoundException.class);

        assertThat(requestRepository.count()).isZero();
    }

    @Test
    void createRequest_ShouldSetCorrectTimestamp() {
        String description = "Need a hammer drill";
        Instant beforeCreation = Instant.now().minusSeconds(1);

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertThat(createdRequest.created()).isNotNull();
        assertThat(createdRequest.created()).isAfter(beforeCreation);
        assertThat(createdRequest.created()).isBefore(Instant.now().plusSeconds(1));
    }

    @Test
    void createRequest_ShouldReturnRequestWithAllFieldsFilled() {
        String description = "Need a grinding machine";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertThat(createdRequest.id()).isNotNull();
        assertThat(createdRequest.description()).isEqualTo(description);
        assertThat(createdRequest.requesterId()).isEqualTo(userId);
        assertThat(createdRequest.created()).isNotNull();
        assertThat(createdRequest.items()).isNull();
    }

    @Test
    void createRequest_ShouldHandleEmptyDescription() {
        String emptyDescription = "";

        RequestDto createdRequest = requestService.createRequest(userId, emptyDescription);

        assertThat(createdRequest.description()).isEqualTo(emptyDescription);
    }

    @Test
    void createRequest_ShouldHandleMultipleRequestsFromSameUser() {
        String description1 = "First request";
        String description2 = "Second request";

        RequestDto request1 = requestService.createRequest(userId, description1);
        RequestDto request2 = requestService.createRequest(userId, description2);

        assertThat(request1.id()).isNotEqualTo(request2.id());
        assertThat(request1.description()).isEqualTo(description1);
        assertThat(request2.description()).isEqualTo(description2);
        assertThat(request1.requesterId()).isEqualTo(userId);
        assertThat(request2.requesterId()).isEqualTo(userId);

        assertThat(requestRepository.count()).isEqualTo(2);
        assertThat(entityManager.find(Request.class, request1.id())).isNotNull();
        assertThat(entityManager.find(Request.class, request2.id())).isNotNull();
    }

    @Test
    void createRequest_ShouldIncrementIdCorrectly() {
        String description1 = "Request 1";
        String description2 = "Request 2";

        RequestDto request1 = requestService.createRequest(userId, description1);
        RequestDto request2 = requestService.createRequest(userId, description2);

        assertThat(request2.id()).isEqualTo(request1.id() + 1);
    }

    @Test
    void createRequest_ShouldCreateRequestWithItemsListInitiallyEmpty() {
        String description = "Need equipment";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertThat(createdRequest.items()).isNull();
    }

    @Test
    void createRequest_ShouldPersistCorrectRequesterReference() {
        String description = "Need tools";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        Request savedRequest = entityManager.find(Request.class, createdRequest.id());
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getRequester().getId()).isEqualTo(testUser.getId());
        assertThat(savedRequest.getRequester().getName()).isEqualTo(testUser.getName());
    }

    @Test
    void createRequest_ShouldHandleLongDescriptions() {
        String longDescription = "A".repeat(100);

        RequestDto createdRequest = requestService.createRequest(userId, longDescription);

        assertThat(createdRequest.description()).isEqualTo(longDescription);
        assertThat(createdRequest.description().length()).isEqualTo(longDescription.length());
    }

    @Test
    void createRequest_ShouldWorkWithSpecialCharactersInDescription() {
        String description = "Need tool with 100% efficiency! @#$%^&*()";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        assertThat(createdRequest.description()).isEqualTo(description);
    }

    @Test
    void createRequest_ShouldHaveConsistentTimestamps() {
        String description = "Need a tool";

        Instant beforeCreation = Instant.now();
        RequestDto createdRequest = requestService.createRequest(userId, description);
        Instant afterCreation = Instant.now();

        assertThat(createdRequest.created()).isBetween(beforeCreation, afterCreation);
    }

    @Test
    void createRequest_ShouldReturnTimestampWithReasonablePrecision() {
        String description = "Test request";

        RequestDto createdRequest = requestService.createRequest(userId, description);

        long timeDifference = Math.abs(Instant.now().toEpochMilli() - createdRequest.created().toEpochMilli());
        assertThat(timeDifference).isLessThan(2000);
    }
}