package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class CustomRequestException extends RuntimeException {
    private HttpStatus status;

    public CustomRequestException() {
        super();
    }

    public CustomRequestException(String message) {
        super(message);
    }

    public CustomRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomRequestException(Throwable cause) {
        super(cause);
    }

    protected CustomRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
