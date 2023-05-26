package ru.practicum.shareit.user.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;
import ru.practicum.shareit.user.util.MappingUser;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<UserDto> all = service.findAll();
        log.info("Получены все пользователи.");
        return all;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @Positive(message = "{user.controller.idNotPositive}") long id) {
        UserDto user = service.findById(id);
        log.info("Получен пользователь с id {}.", id);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @Positive(message = "{user.controller.idNotPositive}") long id) {
        service.delete(id);
        log.info("Удален пользователь с id {}.", id);
    }

    @PostMapping
    @Validated(value = Create.class)
    public UserDto addUser(@Validated(value = Create.class) @RequestBody UserDto userDto) {
        User user = MappingUser.mapToUser(userDto);
        UserDto createUser = service.create(user);
        log.info("Создан пользователь {}.", createUser);
        return createUser;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated(value = Update.class) @RequestBody UserDto userDto,
                              @PathVariable @Positive(message = "{user.controller.idNotPositive}") long id) {
        userDto.setId(id);
        User user = MappingUser.mapToUser(userDto);
        UserDto updateUser = service.update(user);
        log.info("Обновлен пользователь {}.", updateUser);
        return updateUser;
    }

    @DeleteMapping("/reset")
    public void resetDB() {
        service.resetDb();
    }


}

