package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(long userId);

    UserDto create(User user);

    UserDto update(User user);

    void delete(long userId);

    void resetDb();
}
