package ru.practicum.shareit.booking.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
public class BookingController {

    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookingOutDto> getAllBooking(@RequestHeader(value = "X-Sharer-User-Id")
                                             @Positive(message = "{booking.controller.userIdNotPositive}")
                                             long userId,
                                             @RequestParam Optional<String> state,
                                             @PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                             @RequestParam(name = "from", defaultValue = "0") int from,
                                             @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        List<BookingOutDto> result = service.findBookingsForBooker(userId, state, from, size);
        log.info("Получены все бронирования");
        return result;
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllBookingForOwner(@RequestHeader(value = "X-Sharer-User-Id")
                                                     @Positive(message = "{booking.controller.userIdNotPositive}")
                                                     long userId,
                                                     @RequestParam Optional<String> state,
                                                     @PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        List<BookingOutDto> result = service.findBookingsForOwner(userId, state, from, size);

        log.info("Получены все бронирования");
        return result;
    }


    @GetMapping("{bookingId}")
    public BookingOutDto getBookingDto(@RequestHeader(value = "X-Sharer-User-Id")
                                       @Positive(message = "{booking.controller.userIdNotPositive}")
                                       long userId,
                                       @PathVariable(value = "bookingId")
                                       @Positive(message = "{booking.controller.bookingIdNotPositive}")
                                       long bookingId) {
        BookingOutDto result = service.findBookingById(userId, bookingId);
        log.info("Получено бронирование: {}", result);
        return result;
    }

    @PostMapping
    public BookingOutDto addBooking(@Valid
                                    @RequestBody BookingInDto bookingInDto,
                                    @RequestHeader(value = "X-Sharer-User-Id")
                                    @Positive(message = "{booking.controller.userIdNotPositive}")
                                    long userId) {
        BookingOutDto result = service.create(bookingInDto, userId);
        log.info("Создано бронирование: {}", result);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto changeApproval(@RequestHeader(value = "X-Sharer-User-Id")
                                        @Positive(message = "{booking.controller.userIdNotPositive}")
                                        long userId,
                                        @PathVariable(value = "bookingId")
                                        @Positive(message = "{booking.controller.bookingIdNotPositive}")
                                        long bookingId,
                                        @RequestParam(value = "approved") Optional<Boolean> approved) {
        BookingOutDto result = service.approval(userId, bookingId, approved);
        log.info("Изменено подтверждение бронирования: {}", approved);
        return result;
    }

    @DeleteMapping("/reset")
    public void resetDb() {
        service.resetDb();
    }
}
