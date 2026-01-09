package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item,Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            " LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Collection<Item> searchAvailableItems(@Param("query") String query);

    @Query("SELECT i, " +
            "(SELECT MAX(b.bookingEndDate) FROM Booking b " +
            " WHERE b.item.id = i.id AND b.status = 'APPROVED' AND b.bookingEndDate < CURRENT_TIMESTAMP) as lastBooking, " +
            "(SELECT MIN(b.bookingStartDate) FROM Booking b " +
            " WHERE b.item.id = i.id AND b.status = 'APPROVED' AND b.bookingStartDate > CURRENT_TIMESTAMP) as nextBooking " +
            "FROM Item i " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY i.name")
    Collection<Object[]> findItemsWithBookingsByOwnerId(@Param("ownerId") Long ownerId);
}

