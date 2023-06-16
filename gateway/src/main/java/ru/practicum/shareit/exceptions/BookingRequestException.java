package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class BookingRequestException extends CustomRequestException {
    public BookingRequestException() {
        super();
    }

    public BookingRequestException(String message) {
        super(message);
    }

    public BookingRequestException(String message, HttpStatus status) {
        super(message, status);
    }
}
