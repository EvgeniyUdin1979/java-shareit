package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    List<ItemOutDtoForGet> findAllByUserId(long userId);

    ItemOutDtoForGet findById(long itemId, long userId);

    List<ItemOutDto> search(String text);

    ItemOutDto create(ItemInDto itemInDto, long ownerId);

    ItemOutDto update(ItemInDto itemInDto, long itemId, long userId);

    CommentOutDto addComment(long authorId, long itemId, CommentInDto inDto);

    void resetDb();
}
