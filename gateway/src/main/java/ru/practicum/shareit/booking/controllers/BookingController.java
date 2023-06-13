package ru.practicum.shareit.booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.config.CustomLocaleMessenger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient client;

    private final CustomLocaleMessenger messenger;

    @Autowired
    public BookingController(BookingClient client, CustomLocaleMessenger messenger) {
        this.client = client;
        this.messenger = messenger;
    }

    @GetMapping
    public ResponseEntity<Object> getAllBooking(@RequestHeader(value = "X-Sharer-User-Id")
                                                @Positive(message = "{booking.controller.userIdNotPositive}")
                                                long userId,
                                                @RequestParam Optional<String> state,
                                                @PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                                @RequestParam(name = "from", defaultValue = "0") int from,
                                                @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        ParamsGetAll params = ParamsGetAll.builder()
                .userId(userId)
                .state(state)
                .from(from)
                .size(size)
                .build();
        return client.getAllBookings(params);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingForOwner(@RequestHeader(value = "X-Sharer-User-Id")
                                                        @Positive(message = "{booking.controller.userIdNotPositive}")
                                                        long userId,
                                                        @RequestParam Optional<String> state,
                                                        @PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        ParamsGetAll params = ParamsGetAll.builder()
                .userId(userId)
                .state(state)
                .from(from)
                .size(size)
                .build();
        return client.getAllBookingForOwner(params);
    }


    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBookingDto(@RequestHeader(value = "X-Sharer-User-Id")
                                                @Positive(message = "{booking.controller.userIdNotPositive}")
                                                long userId,
                                                @PathVariable(value = "bookingId")
                                                @Positive(message = "{booking.controller.bookingIdNotPositive}")
                                                long bookingId) {
        return client.getBookingDto(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@Valid
                                             @RequestBody BookingInDto bookingInDto,
                                             @RequestHeader(value = "X-Sharer-User-Id")
                                             @Positive(message = "{booking.controller.userIdNotPositive}")
                                             long userId) {
        return client.addBooking(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeApproval(@RequestHeader(value = "X-Sharer-User-Id")
                                                 @Positive(message = "{booking.controller.userIdNotPositive}")
                                                 long userId,
                                                 @PathVariable(value = "bookingId")
                                                 @Positive(message = "{booking.controller.bookingIdNotPositive}")
                                                 long bookingId,
                                                 @RequestParam(value = "approved") Optional<Boolean> approved) {
        return client.changeApproval(userId, bookingId, approved);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Object> resetDb() {
        return client.resetDb();
    }
}
