package ru.practicum.shareit.booking.controllers;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class ParamsGetAll {
    private long userId;
    private Optional<String> state;
    private int from;
    private int size;
}
