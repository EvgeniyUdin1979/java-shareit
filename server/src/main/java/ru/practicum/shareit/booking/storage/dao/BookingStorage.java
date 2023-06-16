package ru.practicum.shareit.booking.storage.dao;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.ParamsFindAll;

import java.util.List;

public interface BookingStorage {
    Booking createBooking(Booking booking);

    Booking findBookingById(long bookingId);

    List<Booking> findAllBookingsForBookerOrOwner(ParamsFindAll params);

    boolean existsId(long bookingId);

    void resetDb();
}
