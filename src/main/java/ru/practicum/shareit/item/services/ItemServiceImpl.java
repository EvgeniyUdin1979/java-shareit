package ru.practicum.shareit.item.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.exceptions.ItemRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.srorages.dao.ItemStorage;
import ru.practicum.shareit.item.util.MappingItem;
import ru.practicum.shareit.user.storages.dao.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CustomLocaleMessenger messenger;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage, CustomLocaleMessenger messenger) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.messenger = messenger;
    }

    @Override
    public List<ItemDto> findAllByUserId(long userId) {
        isExistsUserId(userId);
        return itemStorage.findAllByUserId(userId).stream()
                .map(MappingItem::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(long id) {
        isExistsItemId(id);
        Item item = itemStorage.findById(id);
        return MappingItem.mapToDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text).stream()
                .map(MappingItem::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Item item) {
        isExistsUserId(item.getOwnerId());
        Item createdItem = itemStorage.create(item);
        return MappingItem.mapToDto(createdItem);
    }

    @Override
    public ItemDto update(Item item) {
        isExistsItemId(item.getId());
        isExistsUserId(item.getOwnerId());
        Item old = itemStorage.findById(item.getId());
        if (old.getOwnerId() != item.getOwnerId()) {
            String message = String.format(messenger.getMessage("item.service.notOwner"), item.getId());
            log.info("У предмета с id {} другой хозяин.", item.getId());
            throw new ItemRequestException(message, HttpStatus.NOT_FOUND);
        }
        Item forUpdate = MappingItem.mapToUpdate(item, old);
        Item updateItem = itemStorage.update(forUpdate);
        return MappingItem.mapToDto(updateItem);
    }

    @Override
    public void resetDb() {
        itemStorage.resetDb();
    }

    private void isExistsItemId(long id) {
        if (!itemStorage.existsId(id)) {
            String message = String.format(messenger.getMessage("item.service.notExistId"), id);
            log.info("Предмет с id {} не найден.", id);
            throw new ItemRequestException(message, HttpStatus.NOT_FOUND);
        }
    }

    private void isExistsUserId(long id) {
        if (!userStorage.existsId(id)) {
            String message = String.format(messenger.getMessage("item.service.notExistUserId"), id);
            log.info("Пользователь с id {} не найден.", id);
            throw new ItemRequestException(message, HttpStatus.NOT_FOUND);
        }
    }
}
