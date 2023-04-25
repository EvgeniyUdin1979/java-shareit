package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.validatrors.ValidIdConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private long id;

    private String name;

    @Email(message = "{user.email}")
    private String email;
}
