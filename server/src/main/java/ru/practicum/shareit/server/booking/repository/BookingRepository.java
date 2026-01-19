package ru.practicum.shareit.server.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.booking.enums.BookingStatus;
import ru.practicum.shareit.server.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND (" +
            "    :state = 'ALL' OR " +
            "    (:state = 'CURRENT' AND :now BETWEEN b.bookingStartDate AND b.bookingEndDate) OR " +
            "    (:state = 'PAST' AND b.bookingEndDate < :now) OR " +
            "    (:state = 'FUTURE' AND b.bookingStartDate > :now) OR " +
            "    (:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "    (:state = 'REJECTED' AND b.status = 'REJECTED')" +
            ") " +
            "ORDER BY b.bookingStartDate DESC")
    Page<Booking> findByBookerIdAndState(
            @Param("bookerId") Long bookerId,
            @Param("state") String state,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND (" +
            "    :state = 'ALL' OR " +
            "    (:state = 'CURRENT' AND :now BETWEEN b.bookingStartDate AND b.bookingEndDate) OR " +
            "    (:state = 'PAST' AND b.bookingEndDate < :now) OR " +
            "    (:state = 'FUTURE' AND b.bookingStartDate > :now) OR " +
            "    (:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "    (:state = 'REJECTED' AND b.status = 'REJECTED')" +
            ") " +
            "ORDER BY b.bookingStartDate DESC")
    Page<Booking> findByOwnerIdAndState(
            @Param("ownerId") Long ownerId,
            @Param("state") String state,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    Collection<Booking> findByItemIdAndBookerIdAndStatus(
            Long itemId,
            Long bookerId,
            BookingStatus status);

    @Query("SELECT MAX(b.bookingEndDate) FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.bookingEndDate < :currentTime")
    Optional<LocalDateTime> findLastBookingEndDate(
            @Param("itemId") Long itemId,
            @Param("status") BookingStatus status,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT MIN(b.bookingStartDate) FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.bookingStartDate > :currentTime")
    Optional<LocalDateTime> findNextBookingStartDate(
            @Param("itemId") Long itemId,
            @Param("status") BookingStatus status,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.id = :id")
    Optional<Booking> findByIdWithItem(@Param("id") Long id);
}
