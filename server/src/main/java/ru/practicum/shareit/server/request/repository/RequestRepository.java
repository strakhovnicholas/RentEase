package ru.practicum.shareit.server.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.request.model.Request;

import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT DISTINCT r FROM Request r " +
            "LEFT JOIN FETCH r.requester " +
            "LEFT JOIN FETCH r.items i " +
            "WHERE r.requester.id = :userId " +
            "ORDER BY r.createdAt DESC")
    Slice<Request> findUserOwnRequests(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Request r LEFT JOIN FETCH r.requester WHERE r.requester.id <> :userId")
    Slice<Request> findRequestsToUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT r FROM Request r " +
            "LEFT JOIN FETCH r.requester " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.id = :id")
    Optional<Request> findRequestById(@Param("id") Long id);
}
