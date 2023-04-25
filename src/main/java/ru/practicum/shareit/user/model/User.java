package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {

    private long id;

    @NotBlank(message = "{user.name.NotBlank}")
    private String name;

    @Email(message = "{user.email}")
    @NotBlank(message = "{user.email.NotBlank}")
    private String email;
}
