package ru.practicum.shareit.server.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.server.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> findUserOwnRequests(Long requestorId, Pageable pageable);

    List<RequestDto> findOtherUsersRequests(Long requestorId, Pageable pageable);

    RequestDto findRequestById(Long id);

    RequestDto createRequest(Long userId, String description);
}
