package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.controllers.ParamsGetAll;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.exceptions.BookingRequestException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";
    private final CustomLocaleMessenger messenger;

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, CustomLocaleMessenger messenger) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.messenger = messenger;
    }

    public ResponseEntity<Object> getAllBookings(ParamsGetAll params) {
        State state = State.from(params.getState().orElse("all")).orElseThrow(() -> {
            String message = String.format(messenger.getMessage("booking.service.errorStateName"), params.getState().get());
            log.debug("Не верный параметр запроса state: {}", params.getState().get());
            return new BookingRequestException(message);
        });
        return get("?state={state}&from={from}&size={size}", params.getUserId(),
                Map.of("from", params.getFrom(),
                        "size", params.getSize(),
                        "state", state.name()));
    }

    public ResponseEntity<Object> getAllBookingForOwner(ParamsGetAll params) {
        State state = State.from(params.getState().orElse("all")).orElseThrow(() -> {
            String message = String.format(messenger.getMessage("booking.service.errorStateName"), params.getState().get());
            log.debug("Не верный параметр запроса state: {}", params.getState().get());
            return new BookingRequestException(message);
        });
        return get("/owner?state={state}&from={from}&size={size}", params.getUserId(),
                Map.of("from", params.getFrom(),
                        "size", params.getSize(),
                        "state", state.name()));
    }

    public ResponseEntity<Object> getBookingDto(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> addBooking(long userId, BookingInDto bookingInDto) {
        LocalDateTime start = bookingInDto.getStart();
        LocalDateTime end = bookingInDto.getEnd();
        if (start.isEqual(end)) {
            String message = messenger.getMessage("booking.service.timeIsEqual");
            log.warn("Время начала бронирования равно окончанию: {}", bookingInDto);
            throw new BookingRequestException(message);
        } else if (start.isAfter(end)) {
            String message = messenger.getMessage("booking.service.startIsAfterEnd");
            log.warn("Время начала бронирования после окончанию: {}", bookingInDto);
            throw new BookingRequestException(message);
        }
        return post("", userId, bookingInDto);
    }

    public ResponseEntity<Object> changeApproval(long userId, long bookingId, Optional<Boolean> approved) {
        Boolean isApproved = approved.orElseThrow(() -> {
            String message = String.format(messenger.getMessage("booking.service.missingApproved"), userId, bookingId);
            log.warn("Отсутствует обязательный параметр запроса approved.");
            return new BookingRequestException(message);
        });
        return patch("/" + bookingId + "?approved={approved}", userId, Map.of("approved", isApproved), null);
    }

    public ResponseEntity<Object> resetDb() {
        return delete("/reset");
    }
}
