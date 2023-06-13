package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemOutDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

}
