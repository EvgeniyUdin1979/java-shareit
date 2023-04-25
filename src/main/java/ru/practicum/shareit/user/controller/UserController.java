package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validatrors.ValidIdConstraint;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;



@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserService service;

    //    @Value("${user.controller.findAll}")
    private String findAll = "{user.controller.findAll}";
    //    @Value("${user.controller.findById}")
    private String findById = "{user.controller.findById}";
    //    @Value("${user.controller.deleteById}")
    private String deleteById = "{user.controller.deleteById}";
    //    @Value("${user.controller.create}")
    private String create = "{user.controller.create}";
    //    @Value("${user.controller.update}")
    private String update = "{user.controller.update}";

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<UserDto> all = service.findAll();
        log.info(findAll);
        return all;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        UserDto user = service.findById(id);
        log.info(findById, id);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable  @Positive(message = "{user}") long id) {
        service.delete(id);
        log.info(deleteById, id);
    }

    @PostMapping
    public UserDto create(@RequestBody User user) {
        UserDto userDto = service.create(user);
        log.info(create, userDto);
        return userDto;
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto user,
                          @PathVariable @ValidIdConstraint(message = "{user.controller.idNotPositive}") long id) {
        user.setId(id);
        UserDto userDto = service.update(user);
        log.info(update, userDto);
        return userDto;
    }

    @DeleteMapping("/reset")
    public void resetDB() {
        service.resetDb();
    }


}

