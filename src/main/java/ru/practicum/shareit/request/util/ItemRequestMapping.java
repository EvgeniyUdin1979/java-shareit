package ru.practicum.shareit.request.util;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemOutDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapping {
    public static ItemRequest mapToItemRequestForCreate(ItemRequestInDto dto, User requestor) {
        return ItemRequest
                .builder()
                .requestor(requestor)
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestOutDto mapToDto(ItemRequest itemRequest, List<Item> itemResponses) {
        return ItemRequestOutDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemResponses.stream().map(r ->
                        ItemOutDto.builder()
                                .id(r.getId())
                                .name(r.getName())
                                .description(r.getDescription())
                                .available(r.getAvailable())
                                .requestId(r.getRequestId())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

}
