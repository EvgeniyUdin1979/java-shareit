package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private long id;

    private String name;

    @Email(groups = {Create.class, Update.class}, message = "{user.dto.email}")
    @NotBlank(groups = {Create.class}, message = "{user.dto.notBlankEmail}")
    private String email;
}
