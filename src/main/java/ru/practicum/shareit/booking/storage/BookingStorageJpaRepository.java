package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingStorageJpaRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM bookings;ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;", nativeQuery = true)
    void clearDb();

}
