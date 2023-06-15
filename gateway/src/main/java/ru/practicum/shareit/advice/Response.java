package ru.practicum.shareit.advice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {

    private String message;

    public Response() {
    }

    public Response(String message) {
        this.message = message;
    }

}
