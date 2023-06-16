package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingOutDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemOutDto item;
    private BookerOutDto booker;
    private Status status;

}
