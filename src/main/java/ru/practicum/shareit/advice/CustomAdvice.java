package ru.practicum.shareit.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exceptions.CustomRequestException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class CustomAdvice {

//    @Value("${advice.typeMismatchException}")
    private String typeMismatchException = "{advice.typeMismatchException}";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleBindException(ConstraintViolationException cve) {
        List<ConstraintViolation<?>> constraintViolations = new ArrayList<>(cve.getConstraintViolations());
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            message.append(constraintViolation.getMessageTemplate());
        }
        log.info(message.toString().trim());
        return new ResponseEntity<>(new Response(message.toString().trim()), HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>(new Response(message.toString().trim()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> typeMismatchException(MethodArgumentTypeMismatchException ex){
        log.info(typeMismatchException);
        return getResponse(HttpStatus.BAD_REQUEST,typeMismatchException);
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

