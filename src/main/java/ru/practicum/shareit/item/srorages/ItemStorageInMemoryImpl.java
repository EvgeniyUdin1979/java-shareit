package ru.practicum.shareit.item.srorages;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.srorages.dao.ItemStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Repository
public class ItemStorageInMemoryImpl implements ItemStorage {

    HashMap<Long, Item> items = new HashMap<>();

    private long globalId = 1;

    @Override
    public List<Item> findAllByUserId(long userId) {

        return items.values().stream().filter(item -> item.getOwnerId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item findById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) && item.getAvailable())
                        || (item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) && item.getAvailable())).collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(getCurrentGlobalId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public boolean existsId(long id) {
        return items.containsKey(id);
    }

    @Override
    public void resetDb() {
        items.clear();
        globalId = 1;
    }

    private long getCurrentGlobalId() {
        return globalId++;
    }
}
