package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class UserRequestException extends CustomRequestException{
    public UserRequestException() {
        super();
    }

    public UserRequestException(String message) {
        super(message);
    }

    public UserRequestException(String message, HttpStatus status) {
        super(message, status);
    }
}
