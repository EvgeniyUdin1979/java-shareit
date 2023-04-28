package ru.practicum.shareit.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.exceptions.CustomRequestException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class CustomAdvice {

    public final CustomLocaleMessenger messenger;

    public CustomAdvice(CustomLocaleMessenger messenger) {
        this.messenger = messenger;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleBindException(ConstraintViolationException cve) {
        List<ConstraintViolation<?>> constraintViolations = new ArrayList<>(cve.getConstraintViolations());
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            builder.append(constraintViolation.getMessage());
        }
        String message = builder.toString().trim();
        log.info(message);
        return getResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(CustomRequestException.class)
    public ResponseEntity<Response> handleUserException(CustomRequestException re) {
        return getResponse(re.getStatus(), re.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleException(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        Object target = ex.getBindingResult().getTarget();
        for (ObjectError error : ex.getAllErrors()) {
            message.append(error.getDefaultMessage());
        }

        log.info(message.toString().trim());
        return getResponse(HttpStatus.BAD_REQUEST, message.toString().trim());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> typeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = messenger.getMessage("advice.typeMismatchException");
        log.info(message);
        return getResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    private ResponseEntity<Response> typeMismatchException(MissingRequestHeaderException ex) {
        String message = messenger.getMessage("advice.missingRequestHeader");
        log.info(message);
        return getResponse(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Response> getResponse(HttpStatus httpStatus, String message) {
        Response response = new Response(message);
        HttpStatus status;
        if (httpStatus != null) {
            status = httpStatus;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }
}

