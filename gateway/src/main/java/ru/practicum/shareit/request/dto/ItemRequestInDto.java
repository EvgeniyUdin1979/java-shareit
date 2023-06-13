package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemRequestInDto {
    @NotBlank(message = "{itemrequest.dto.notBlankDescription}")
    private String description;
}
