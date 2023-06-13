package ru.practicum.shareit.booking.dto;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class BookingInDto {

    @Positive(message = "{booking.dto.itemIdNotPositive}")
    private long itemId;

    @Future(message = "{booking.dto.startDataFuture}")
    @NotNull(message = "{booking.dto.startDataNotNull}")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private LocalDateTime start;

    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Future(message = "{booking.dto.endDataFuture}")
    @NotNull(message = "{booking.dto.endDataNotNull}")
    private LocalDateTime end;


}
