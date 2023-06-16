package ru.practicum.shareit.booking.storage;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.State;

@Data
@Builder
public class ParamsFindAll {
    private boolean isBooker;
    private long userId;
    private State state;
    private int from;
    private int size;
}
