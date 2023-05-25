package ru.practicum.shareit.item.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.dao.BookingStorage;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.exceptions.ItemRequestException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.srorages.dao.ItemStorage;
import ru.practicum.shareit.item.util.MappingItem;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storages.dao.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CustomLocaleMessenger messenger;

    @Autowired
    public ItemServiceImpl(@Qualifier(value = "itemJpa") ItemStorage itemStorage,
                           @Qualifier(value = "userJpa") UserStorage userStorage,
                           BookingStorage bookingStorage, CustomLocaleMessenger messenger) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.messenger = messenger;
    }

    @Override
    @Transactional
    public List<ItemOutDtoForGet> findAllByUserId(long userId) {
        isExistsUserId(userId);
        return itemStorage.findAllByUserId(userId).stream()
                .map(item ->
                        MappingItem
                                .mapToDtoForGet(
                                        item,
                                        itemStorage.findLastBooking(item.getId(), userId),
                                        itemStorage.findNextBooking(item.getId(), userId)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemOutDtoForGet findById(long itemId, long userId) {
        isExistsItemId(itemId);
        Item item = itemStorage.findItemById(itemId);
        Booking next = itemStorage.findNextBooking(itemId, userId);
        Booking last = itemStorage.findLastBooking(itemId, userId);
        return MappingItem.mapToDtoForGet(item, last, next);
    }

    @Override
    @Transactional
    public List<ItemOutDto> search(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.search(text).stream()
                .map(MappingItem::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemOutDto create(ItemInDto itemInDto, long ownerId) {
        isExistsUserId(ownerId);
        Item item = MappingItem.mapToItem(itemInDto);
        User owner = userStorage.findUserById(ownerId);
        item.setOwner(owner);
        Item createdItem = itemStorage.create(item);
        return MappingItem.mapToDto(createdItem);
    }

    @Override
    @Transactional
    public ItemOutDto update(ItemInDto itemInDto, long itemId, long ownerId) {
        isExistsItemId(itemId);
        isExistsUserId(ownerId);
        Item old = itemStorage.findItemById(itemId);
        if (old.getOwner().getId() != ownerId) {
            String message = String.format(messenger.getMessage("item.service.notOwner"), itemId);
            log.info("У предмета с id {} другой хозяин.", itemId);
            throw new ItemRequestException(message, HttpStatus.NOT_FOUND);
        }
        MappingItem.mapToUpdate(itemInDto, old);
        Item updateItem = itemStorage.update(old);
        return MappingItem.mapToDto(updateItem);
    }

    @Override
    @Transactional
    public CommentOutDto addComment(long authorId, long itemId, CommentInDto inDto) {
        userStorage.existsId(authorId);
        itemStorage.existsId(itemId);
        List<Booking> allPastBookings = bookingStorage.findAllBookingsForBookerOrOwner(true, authorId, State.PAST);
        if (allPastBookings.size() == 0 || allPastBookings.stream().noneMatch(b -> b.getStatus() == Status.APPROVED)) {
            String message = String.format(messenger.getMessage("item.service.notBookingForItem"), authorId, itemId);
            log.info("У пользователя с id: {} нет подтвержденных завершенных бронирований на предмет с id: {}", authorId, itemId);
            throw new ItemRequestException(message);
        }
        return MappingItem.mapCommentToDto(itemStorage.add(authorId, itemId, inDto.getText()));
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
