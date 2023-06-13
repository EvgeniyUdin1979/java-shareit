package ru.practicum.shareit.request.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.exceptions.ItemRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storages.dao.ItemRequestStorage;
import ru.practicum.shareit.request.util.ItemRequestMapping;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storages.dao.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {


    private final ItemRequestStorage requestStorage;

    private final UserStorage userStorage;

    private final CustomLocaleMessenger messenger;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestStorage requestStorage, UserStorage userStorage, CustomLocaleMessenger messenger) {
        this.requestStorage = requestStorage;
        this.userStorage = userStorage;
        this.messenger = messenger;
    }


    @Override
    @Transactional
    public ItemRequestOutDto create(ItemRequestInDto itemRequestInDto, long userId) {
        isExistsUserId(userId);
        User requestor = userStorage.findUserById(userId);
        ItemRequest newest = ItemRequestMapping.mapToItemRequestForCreate(itemRequestInDto, requestor);
        ItemRequest result = requestStorage.create(newest);
        List<Item> items = requestStorage.findAllItemIdByRequestId(result.getId());
        return ItemRequestMapping.mapToDto(result, items);
    }

    @Override
    @Transactional
    public List<ItemRequestOutDto> findAllByRequestor(long userId) {
        isExistsUserId(userId);
        List<ItemRequest> result = requestStorage.findAllByRequestorId(userId);
        return result.stream().map(itemRequest -> {
            List<Item> items = requestStorage.findAllItemIdByRequestId(itemRequest.getId());
            return ItemRequestMapping.mapToDto(itemRequest, items);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemRequestOutDto> findAll(int from, int size, long userId) {
        List<ItemRequest> result = requestStorage.findAll(from, size, userId);
        return result.stream()
                .peek(itemRequest -> {
                    List<Item> items = requestStorage.findAllItemIdByRequestId(itemRequest.getId());
                    itemRequest.setItems(items);
                })
                .map(r -> ItemRequestMapping.mapToDto(r, r.getItems()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestOutDto findById(long requestId, long userId) {
        isExistsItemRequest(requestId);
        isExistsUserId(userId);
        ItemRequest result = requestStorage.findById(requestId);
        List<Item> items = requestStorage.findAllItemIdByRequestId(result.getId());
        return ItemRequestMapping.mapToDto(result, items);
    }

    @Override
    public void resetDb() {
        requestStorage.resetDb();
    }

    private void isExistsItemRequest(long requestId) {
        if (!requestStorage.isExists(requestId)) {
            String message = String.format(messenger.getMessage("itemRequest.service.notExistId"), requestId);
            log.warn("Запрос на бронирование с id: {} не найдено.", requestId);
            throw new ItemRequestException(message, HttpStatus.NOT_FOUND);
        }
    }

    private void isExistsUserId(long id) {
        if (!userStorage.existsId(id)) {
            String message = String.format(messenger.getMessage("item.service.notExistUserId"), id);
            log.warn("Пользователь с id {} не найден.", id);
            throw new ItemRequestException(message, HttpStatus.NOT_FOUND);
        }
    }
}
