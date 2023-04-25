package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.dao.UserDao;
import ru.practicum.shareit.user.util.MappingUser;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.util.MappingUser.mapDtoToUser;
import static ru.practicum.shareit.user.util.MappingUser.mapUserToDto;

@Service
@Slf4j
public class UserService {

    private final UserDao userDao;

//    @Value("${user.service.notFindUserById}")
    private String notFindUserById = "{user.service.notFindUserById}";

//    @Value("${user.service.existsEmail}")
    private String existsEmail = "{user.service.existsEmail}";

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<UserDto> findAll() {
        return userDao.findAll().stream().map(MappingUser::mapUserToDto).collect(Collectors.toList());
    }

    public UserDto findById(long userId) {
        existsId(userId);
        return mapUserToDto(userDao.findById(userId).get());
    }

    public UserDto create(User user) {
        existsEmail(user.getEmail());
        return mapUserToDto(userDao.add(user));
    }

    public UserDto update(UserDto userDto) {
        existsId(userDto.getId());
        UserDto userOld = findById(userDto.getId());
        if (userDto.getEmail() != null && !userOld.getEmail().equals(userDto.getEmail())) {
            existsEmail(userDto.getEmail());
        }
        User user = mapDtoToUser(userDto, userDao.findById(userDto.getId()).get());
        return mapUserToDto(userDao.update(user));
    }

    public void delete(long userId) {
        existsId(userId);
        userDao.deleteById(userId);
    }

    public void resetDb() {
        userDao.resetDB();
    }

    private void existsId(long userId) {
        if (!userDao.existsId(userId)) {
            String message = String.format(notFindUserById, userId);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.NOT_FOUND);
        }
    }

    private void existsEmail(String email) {
        if (userDao.existsEmail(email)) {
            String message = String.format(existsEmail, email);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.CONFLICT);
        }
    }
}

