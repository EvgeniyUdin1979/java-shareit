package ru.practicum.shareit.user.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.config.CustomLocaleMessenger;
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
    private final CustomLocaleMessenger messenger;

    @Autowired
    public UserServiceImpl(@Qualifier(value = "userJpa") UserStorage userStorage, CustomLocaleMessenger messenger) {
        this.userStorage = userStorage;
        this.messenger = messenger;
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream().map(MappingUser::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(long userId) {
        isExistsId(userId);
        return mapToDto(userStorage.findUserById(userId));
    }

    @Override
    public UserDto create(User user) {
//        isExistsEmail(user.getEmail());
        return mapToDto(userStorage.add(user));
    }

    @Override
    public UserDto update(User user) {
        isExistsId(user.getId());
        User userOld = userStorage.findUserById(user.getId());
        if (user.getEmail() != null && !user.getEmail().equals(userOld.getEmail())) {
            isExistsEmail(user.getEmail());
        }
        User updateUser = userStorage.update(mapToUpdate(user, userOld));
        return mapToDto(updateUser);
    }

    @Override
    public void delete(long userId) {
        isExistsId(userId);
        userStorage.deleteById(userId);
    }

    @Override
    public void resetDb() {
        userStorage.resetDB();
    }

    private void isExistsId(long userId) {
        if (!userStorage.existsId(userId)) {
            String message = String.format(messenger.getMessage("user.service.notFindUserById"), userId);
            log.info(String.format("Пользователь с id %d не найден.",userId));
            throw new UserRequestException(message, HttpStatus.NOT_FOUND);
        }
    }

    private void isExistsEmail(String email) {
        if (userStorage.existsEmail(email)) {
            String message = String.format(messenger.getMessage("service.existsEmail"), email);
            log.info(String.format("Пользователь с email %s уже существует.",email));
            throw new UserRequestException(message, HttpStatus.CONFLICT);
        }
    }
}

