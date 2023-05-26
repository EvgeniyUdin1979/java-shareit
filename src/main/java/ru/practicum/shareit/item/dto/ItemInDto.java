package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validate.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemInDto {

    private long id;

    @NotBlank(groups = {Create.class}, message = "{item.dto.name.NotBlank}")
    private String name;

    @NotBlank(groups = {Create.class}, message = "{item.dto.description.NotBlank}")
    private String description;

    @NotNull(groups = {Create.class}, message = "{item.dto.available.NotNull}")
    private Boolean available;
}
