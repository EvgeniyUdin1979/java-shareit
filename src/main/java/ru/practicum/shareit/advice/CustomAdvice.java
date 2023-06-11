package ru.practicum.shareit.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.exceptions.CustomRequestException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class CustomAdvice {

    public final CustomLocaleMessenger messenger;

    public CustomAdvice(CustomLocaleMessenger messenger) {
        this.messenger = messenger;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleBindException(ConstraintViolationException cve) {
        List<String> errorMessages = cve.getConstraintViolations().stream().map(ConstraintViolation::getMessage).sorted().collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        for (String errorMessage : errorMessages) {
            builder.append(errorMessage);
        }
        String message = builder.toString().trim();
        log.warn(message);
        return getResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(CustomRequestException.class)
    public ResponseEntity<?> handleUserException(CustomRequestException re) {
        if (re.getMessage().startsWith("Не верный параметр запроса state")) {
            return new ResponseEntity<>(new Error("Unknown state: UNSUPPORTED_STATUS"), HttpStatus.BAD_REQUEST);
        }
        return getResponse(re.getStatus(), re.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getAllErrors().stream().map(e -> e.getDefaultMessage()).sorted().collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        for (String errorMessage : errorMessages) {
            builder.append(errorMessage);
        }
        String message = builder.toString().trim();
        log.warn(message);
        return getResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleException(DataIntegrityViolationException ex) {
        String message = messenger.getMessage("service.existsEmail");
        return getResponse(HttpStatus.CONFLICT, message);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> typeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = messenger.getMessage("advice.typeMismatchException");
        log.warn("Параметр id не является числом. {}", ex.getName());
        return getResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    private ResponseEntity<Response> typeMismatchException(MissingRequestHeaderException ex) {
        String message = messenger.getMessage("advice.missingRequestHeader");
        log.warn("Отсутствует указание пользователя для данного запроса.{}", ex.getHeaderName());
        return getResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<Response> typeMismatchException(HttpMessageNotReadableException ex) {
        String message = messenger.getMessage("advice.missingRequestBody");
        log.warn("Отсутствует тело(json) для данного запроса.");
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

