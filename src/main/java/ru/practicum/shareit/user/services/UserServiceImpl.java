package ru.practicum.shareit.user.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storages.dao.UserStorage;
import ru.practicum.shareit.user.util.MappingUser;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.util.MappingUser.mapToDto;
import static ru.practicum.shareit.user.util.MappingUser.mapToUpdate;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Value("${user.service.notFindUserById}")
    private String notFindUserById;

    @Value("${user.service.existsEmail}")
    private String existsEmail;
    @Value("${user.service.notNullEmail}")
    private String notNullEmail;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> findAll() {
        return userStorage.findAll().stream().map(MappingUser::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(long userId) {
        existsId(userId);
        return mapToDto(userStorage.findById(userId).get());
    }

    @Override
    public UserDto create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserRequestException(notNullEmail);
        }
        existsEmail(user.getEmail());
        return mapToDto(userStorage.add(user));
    }

    @Override
    public UserDto update(User user) {
        existsId(user.getId());
        User userOld = userStorage.findById(user.getId()).get();
        if (user.getEmail() != null && !user.getEmail().equals(userOld.getEmail())) {
            existsEmail(user.getEmail());
        }
        User updateUser = userStorage.update(mapToUpdate(user, userOld));
        return mapToDto(updateUser);
    }

    @Override
    public void delete(long userId) {
        existsId(userId);
        userStorage.deleteById(userId);
    }

    @Override
    public void resetDb() {
        userStorage.resetDB();
    }

    private void existsId(long userId) {
        if (!userStorage.existsId(userId)) {
            String message = String.format(notFindUserById, userId);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.NOT_FOUND);
        }
    }

    private void existsEmail(String email) {
        if (userStorage.existsEmail(email)) {
            String message = String.format(existsEmail, email);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.CONFLICT);
        }
    }
}

