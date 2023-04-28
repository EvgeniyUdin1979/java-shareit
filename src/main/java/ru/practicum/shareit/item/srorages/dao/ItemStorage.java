package ru.practicum.shareit.item.srorages.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> findAllByUserId(long userId);

    Item findById(long id);

    List<Item> search(String text);

    Item create(Item item);

    Item update(Item item);

    boolean existsId(long id);

    void resetDb();
}
