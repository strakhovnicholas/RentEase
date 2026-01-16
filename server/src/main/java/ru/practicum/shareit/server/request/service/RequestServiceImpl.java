package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.exception.RequestNotFoundException;
import ru.practicum.shareit.server.request.mapper.RequestMapper;
import ru.practicum.shareit.server.request.model.Request;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<RequestDto> findUserOwnRequests(Long requesterId, Pageable pageable) {
        log.info("Looking for requests of user: {}", requesterId);

        Slice<Request> requestsSlice = requestRepository.findUserOwnRequests(requesterId, pageable);

        log.info("Found {} requests for user {}", requestsSlice.getNumberOfElements(), requesterId);
        log.info("Total elements: {}", requestsSlice.getNumberOfElements());

        return requestsSlice.getContent().stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> findOtherUsersRequests(Long requesterId, Pageable pageable) {
        Slice<Request> requestsSlice = requestRepository.findRequestsToUser(requesterId, pageable);
        return requestsSlice.getContent().stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto findRequestById(Long requestId) {
        return requestRepository.findRequestById(requestId)
                .map(requestMapper::toDto)
                .orElseThrow(() -> new RequestNotFoundException("Request not found: " + requestId));
    }

    @Override
    public RequestDto createRequest(Long userId, String description) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Request request = Request.builder()
                .requester(owner)
                .createdAt(Instant.now())
                .description(description)
                .build();

        requestRepository.save(request);

        return this.requestMapper.toDto(request);
    }
}
