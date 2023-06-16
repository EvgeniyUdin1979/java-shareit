package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.dto.BookerOutDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.ItemOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapping {

    public static Booking mapToBooking(BookingInDto dto, User booker, Item item) {
        return Booking.builder()
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .booker(booker)
                .item(item)
                .status(Status.WAITING)
                .build();
    }

    public static BookingOutDto mapToDto(Booking booking) {
        BookerOutDto bod = new BookerOutDto();
        bod.setId(booking.getBooker().getId());

        ItemOutDto iod = new ItemOutDto();
        iod.setId(booking.getItem().getId());
        iod.setName(booking.getItem().getName());

        return BookingOutDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(bod)
                .item(iod)
                .build();
    }
}
