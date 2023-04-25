package ru.practicum.shareit.user.util;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class MappingUser {
    public static User mapDtoToUser(UserDto userDto, User user){
        if (userDto.getName() != null){
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null){
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    public static UserDto mapUserToDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
