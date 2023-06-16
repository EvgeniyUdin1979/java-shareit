package ru.practicum.shareit.user.util;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class MappingUser {
    public static User mapToUser(UserDto userDto) {

        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToUpdate(User newest, User old) {
        if (newest.getName() != null) {
            old.setName(newest.getName());
        }
        if (newest.getEmail() != null) {
            old.setEmail(newest.getEmail());
        }
        return old;
    }

}
