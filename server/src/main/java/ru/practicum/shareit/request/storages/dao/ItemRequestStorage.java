package ru.practicum.shareit.request.storages.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestStorage {
    ItemRequest create(ItemRequest itemRequest);

    List<ItemRequest> findAllByRequestorId(long requestorId);

    List<ItemRequest> findAll(int from, int size, long userId);

    ItemRequest findById(long requestId);

    List<Item> findAllItemIdByRequestId(long requestId);

    boolean isExists(long requestId);

    void resetDb();


}
