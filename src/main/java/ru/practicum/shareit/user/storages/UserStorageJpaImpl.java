package ru.practicum.shareit.user.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storages.dao.UserStorage;

import java.util.List;

@Repository
public class UserStorageJpaImpl implements UserStorage {

    @Autowired
    UserStorageJpaRepository repository;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findUserById(long userId) {
        return repository.findById(userId).get();
    }

    @Override
    public User add(User user) {
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        return repository.save(user);
    }

    @Override
    public void deleteById(long userId) {
        repository.deleteById(userId);
    }

    @Override
    public boolean existsEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsId(long id) {
        return repository.existsById(id);
    }

    @Override
    public void resetDB() {
        repository.clearDb();
    }
}
