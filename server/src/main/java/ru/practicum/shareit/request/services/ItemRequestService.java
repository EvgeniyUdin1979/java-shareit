package ru.practicum.shareit.request.services;

import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestOutDto create(ItemRequestInDto itemRequestInDto, long userId);

    List<ItemRequestOutDto> findAllByRequestor(long userId);

    List<ItemRequestOutDto> findAll(int from, int size, long userId);

    ItemRequestOutDto findById(long requestId, long userId);

    void resetDb();
}
