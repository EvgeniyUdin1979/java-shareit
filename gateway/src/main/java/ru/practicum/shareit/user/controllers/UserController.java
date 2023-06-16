package ru.practicum.shareit.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

import javax.validation.constraints.Positive;


@Controller
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserClient client;

    @Autowired
    public UserController(UserClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return client.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive(message = "{user.controller.idNotPositive}") long id) {
        return client.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable @Positive(message = "{user.controller.idNotPositive}") long id) {
        return client.deleteById(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(value = Create.class) @RequestBody UserDto userDto) {
        return client.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Validated(value = Update.class) @RequestBody UserDto userDto,
                              @PathVariable @Positive(message = "{user.controller.idNotPositive}") long id) {
        return client.updateUser(userDto, id);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Object> resetDB() {
        return client.resetDb();
    }


}

