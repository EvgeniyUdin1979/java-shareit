package ru.practicum.shareit.user.storages.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User findById(long userId);

    User add(User user);

    User update(User user);

    void deleteById(long userId);

    boolean existsEmail(String email);

    boolean existsId(long id);

    void resetDB();
}
