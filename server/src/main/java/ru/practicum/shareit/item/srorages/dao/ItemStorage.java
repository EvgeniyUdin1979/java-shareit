package ru.practicum.shareit.item.srorages.dao;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends CommentRepository {

    List<Item> findAllByUserId(long userId, int from, int size);

    Item findItemById(long id);

    List<Item> search(String text, int from, int size);

    Item create(Item item);

    Item update(Item item);

    boolean existsId(long id);

    Booking findNextBooking(long itemId, long userId);

    Booking findLastBooking(long itemId, long userId);

    void resetDb();
}
