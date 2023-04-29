package ru.practicum.shareit.user.storages;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storages.dao.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserStorageInMemoryImpl implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();

    private long globalId = 1L;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long userId) {
        return users.get(userId);
    }

    @Override
    public User add(User user) {
        user.setId(getGlobalId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void deleteById(long userId) {
        users.remove(userId);
    }

    @Override
    public boolean existsEmail(String email) {
        return users.values().stream().anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public boolean existsId(long id) {
        return users.containsKey(id);
    }

    @Override
    public void resetDB() {
        users.clear();
        globalId = 1;
    }

    private long getGlobalId() {
        return globalId++;
    }
}
