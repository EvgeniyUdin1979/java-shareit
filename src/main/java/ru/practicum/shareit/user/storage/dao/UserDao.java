package ru.practicum.shareit.user.storage.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> findAll();

    Optional<User> findById(long userId );

    User add(User user);

    User update(User user);

    void deleteById(long userId);

    boolean existsEmail(String email);

    boolean existsId(long id);

    void resetDB();
}
