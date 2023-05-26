package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.dao.BookingStorage;
import ru.practicum.shareit.booking.util.BookingMapping;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.exceptions.BookingRequestException;
import ru.practicum.shareit.exceptions.ItemRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.srorages.dao.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storages.dao.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final CustomLocaleMessenger messenger;


    @Autowired
    public BookingServiceImpl(BookingStorage bookingStorage,
                              UserStorage userStorage,
                              ItemStorage itemStorage,
                              CustomLocaleMessenger messenger) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.messenger = messenger;
    }


    @Override
    @Transactional
    public BookingOutDto create(BookingInDto bookingInDto, long userId) {
        isExistsUserId(userId);
        long itemId = bookingInDto.getItemId();
        isExistsItemId(itemId);
        Item item = itemStorage.findItemById(itemId);
        if (item.getOwner().getId() == userId) {
            String message = messenger.getMessage("booking.service.ownerNotCreate");
            log.warn("Владелец предмета не может создавать бронирование на собственные вещи!");
            throw new BookingRequestException(message, HttpStatus.NOT_FOUND);
        }
        if (!item.getAvailable()) {
            String message = messenger.getMessage("booking.service.notAvailable");
            log.warn("Данный предмет не доступен для бронирования: {}", bookingInDto);
            throw new BookingRequestException(message);
        }
        LocalDateTime start = bookingInDto.getStart();
        LocalDateTime end = bookingInDto.getEnd();
        if (start.isEqual(end)) {
            String message = messenger.getMessage("booking.service.timeIsEqual");
            log.warn("Время начала бронирования равно окончанию: {}", bookingInDto);
            throw new BookingRequestException(message);
        } else if (start.isAfter(end)) {
            String message = messenger.getMessage("booking.service.startIsAfterEnd");
            log.warn("Время начала бронирования после окончанию: {}", bookingInDto);
            throw new BookingRequestException(message);
        }
        User booker = userStorage.findUserById(userId);
        Booking newest = BookingMapping.mapToBooking(bookingInDto, booker, item);
        Booking booking = bookingStorage.createBooking(newest);
        return BookingMapping.mapToDto(booking);
    }

    @Override
    @Transactional
    public List<BookingOutDto> findBookingsForBooker(long userId, Optional<String> stateIn) {
        isExistsUserId(userId);
        State state = getState(stateIn);
        return bookingStorage.findAllBookingsForBookerOrOwner(true, userId, state)
                .stream().map(BookingMapping::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingOutDto> findBookingsForOwner(long userId, Optional<String> stateIn) {
        isExistsUserId(userId);
        State state = getState(stateIn);
        return bookingStorage.findAllBookingsForBookerOrOwner(false, userId, state)
                .stream().map(BookingMapping::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingOutDto approval(long userId, long bookingId, Optional<Boolean> approved) {
        boolean isApproved = approved.orElseThrow(() -> {
            String message = String.format(messenger.getMessage("booking.service.missingApproved"), userId, bookingId);
            log.warn("Отсутствует обязательный параметр запроса approved.");
            return new BookingRequestException(message);
        });
        isExistsUserId(userId);
        isExistsBookingId(bookingId);
        Booking booking = bookingStorage.findBookingById(bookingId);
        User owner = booking.getItem().getOwner();
        if (owner.getId() != userId) {
            String message = String.format(messenger.getMessage("booking.service.notOwner"), userId, bookingId);
            log.warn("Пользователь id: {} не является хозяином предмета id: {}", userId, bookingId);
            throw new BookingRequestException(message, HttpStatus.NOT_FOUND);
        }
        Status status;
        if (isApproved) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        if (booking.getStatus() == status) {
            String message = String.format(messenger.getMessage("booking.service.statusAgainChange"), status.name());
            log.warn("Статус бронирования уже изменен на: {}.", status.name());
            throw new BookingRequestException(message);
        }
        booking.setStatus(status);
        return BookingMapping.mapToDto(booking);
    }

    @Override
    @Transactional
    public BookingOutDto findBookingById(long userId, long bookingId) {
        isExistsUserId(userId);
        isExistsBookingId(bookingId);
        Booking booking = bookingStorage.findBookingById(bookingId);
        long bookerId = booking.getBooker().getId();
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            String message = String.format(messenger.getMessage("booking.service.NotBooker"), userId, bookingId);
            log.debug("Пользователь id {} не является создателем запроса на бронирование с id {} или владельцем предмета бронирования.", userId, bookingId);
            throw new BookingRequestException(message, HttpStatus.NOT_FOUND);
        }
        return BookingMapping.mapToDto(booking);
    }

    @Override
    public void resetDb() {
        bookingStorage.resetDb();
    }

    private State getState(Optional<String> stateIn) {
        State state = State.ALL;
        if (stateIn.isPresent()) {

            try {
                state = State.valueOf(stateIn.get().toUpperCase());
            } catch (IllegalArgumentException e) {
                String message = String.format(messenger.getMessage("booking.service.errorStateName"), stateIn.get());
                log.debug("Не верный параметр запроса state: {}", stateIn);
                throw new BookingRequestException(message);
            }
        }
        return state;
    }


    private void isExistsItemId(long id) {
        if (!itemStorage.existsId(id)) {
            String message = String.format(messenger.getMessage("item.service.notExistId"), id);
            log.warn("Предмет с id {} не найден.", id);
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

    private void isExistsBookingId(long id) {
        if (!bookingStorage.existsId(id)) {
            String message = String.format(messenger.getMessage("booing.service.notExistId"), id);
            log.warn("Бронирование с id {} не найден.", id);
            throw new ItemRequestException(message, HttpStatus.NOT_FOUND);
        }
    }
}
