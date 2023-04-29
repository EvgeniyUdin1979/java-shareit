package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllByUserId(long userId);

    ItemDto findById(long id);

    List<ItemDto> search(String text);

    ItemDto create(Item item);

    ItemDto update(Item item);

    void resetDb();
}
