package ru.practicum.shareit.booking.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final String headerUserId = "X-Sharer-User-Id";
    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookingOutDto> getAllBooking(@RequestHeader(value = headerUserId)
                                             long userId,
                                             @RequestParam Optional<String> state,
                                             @RequestParam(name = "from", defaultValue = "0") int from,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        ParamsGetAll params = ParamsGetAll.builder()
                .userId(userId)
                .state(state)
                .from(from)
                .size(size)
                .build();
        List<BookingOutDto> result = service.findBookingsForBooker(params);
        log.info("Получены все бронирования");
        return result;
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllBookingForOwner(@RequestHeader(value = headerUserId)
                                                     long userId,
                                                     @RequestParam Optional<String> state,
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        ParamsGetAll params = ParamsGetAll.builder()
                .userId(userId)
                .state(state)
                .from(from)
                .size(size)
                .build();
        List<BookingOutDto> result = service.findBookingsForOwner(params);

        log.info("Получены все бронирования");
        return result;
    }


    @GetMapping("{bookingId}")
    public BookingOutDto getBookingDto(@RequestHeader(value = headerUserId)
                                       long userId,
                                       @PathVariable(value = "bookingId")
                                       long bookingId) {
        BookingOutDto result = service.findBookingById(userId, bookingId);
        log.info("Получено бронирование: {}", result);
        return result;
    }

    @PostMapping
    public BookingOutDto addBooking(@RequestBody BookingInDto bookingInDto,
                                    @RequestHeader(value = headerUserId)
                                    long userId) {
        BookingOutDto result = service.create(bookingInDto, userId);
        log.info("Создано бронирование: {}", result);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto changeApproval(@RequestHeader(value = headerUserId)
                                        long userId,
                                        @PathVariable(value = "bookingId")
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
