package ru.practicum.shareit.item.srorages;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.srorages.dao.ItemStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Repository
public class ItemStorageInMemoryImpl implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();

    private long globalId = 1;

    @Override
    public List<Item> findAllByUserId(long userId) {

        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item findItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
                        || (item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
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
    public Booking findNextBooking(long itemId, long userId) {
        return null;
    }

    @Override
    public Booking findLastBooking(long itemId, long userId) {
        return null;
    }

    @Override
    public Comment add(long userId, long itemId, String text) {
        return null;
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
