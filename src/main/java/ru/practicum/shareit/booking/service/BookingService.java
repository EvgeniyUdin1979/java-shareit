package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingOutDto create(BookingInDto bookingInDto, long userId);

    List<BookingOutDto> findBookingsForBooker(long userId, Optional<String> state);

    BookingOutDto approval(long userId, long bookingId, Optional<Boolean> approved);

    BookingOutDto findBookingById(long userId, long bookingId);

    List<BookingOutDto> findBookingsForOwner(long userId, Optional<String> state);

    void resetDb();
}
