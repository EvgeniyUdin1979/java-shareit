package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.dao.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoMemoryImpl implements UserDao {

    HashMap<Long, User> users = new HashMap<>();

    private long globalId = 1L;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
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
