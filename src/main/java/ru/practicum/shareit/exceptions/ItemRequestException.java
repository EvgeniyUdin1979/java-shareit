package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class ItemRequestException extends CustomRequestException {
    public ItemRequestException() {
        super();
    }

    public ItemRequestException(String message) {
        super(message);
    }

    public ItemRequestException(String message, HttpStatus status) {
        super(message, status);
    }
}
