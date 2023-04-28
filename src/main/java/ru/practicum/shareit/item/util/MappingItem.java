package ru.practicum.shareit.item.util;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class MappingItem {

    public static Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item mapToUpdate(Item newest, Item old) {
        if (newest.getName() != null) {
            old.setName(newest.getName());
        }
        if (newest.getDescription() != null) {
            old.setDescription(newest.getDescription());
        }
        if (newest.getAvailable() != null) {
            old.setAvailable(newest.getAvailable());
        }
        return old;
    }
}
