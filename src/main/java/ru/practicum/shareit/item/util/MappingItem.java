package ru.practicum.shareit.item.util;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

public class MappingItem {

    public static Item mapToItem(ItemInDto itemInDto) {
        return Item.builder()
                .id(itemInDto.getId())
                .name(itemInDto.getName())
                .description(itemInDto.getDescription())
                .available(itemInDto.getAvailable())
                .build();
    }

    public static ItemOutDto mapToDto(Item item) {
        return ItemOutDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemOutDtoForGet mapToDtoForGet(Item item, Booking lastBooking, Booking nextBooking) {
        BookingForItemDto last = null;
        if (lastBooking != null) {
            last = new BookingForItemDto();
            last.setId(lastBooking.getId());
            last.setBookerId(lastBooking.getBooker().getId());
        }
        BookingForItemDto next = null;
        if (nextBooking != null) {
            next = new BookingForItemDto();
            next.setId(nextBooking.getId());
            next.setBookerId(nextBooking.getBooker().getId());
        }

        return ItemOutDtoForGet.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(item.getComments().stream().map(MappingItem::mapCommentToDto).collect(Collectors.toList()))
                .build();
    }

    public static Item mapToUpdate(ItemInDto newestDto, Item old) {
        if (newestDto.getName() != null) {
            old.setName(newestDto.getName());
        }
        if (newestDto.getDescription() != null) {
            old.setDescription(newestDto.getDescription());
        }
        if (newestDto.getAvailable() != null) {
            old.setAvailable(newestDto.getAvailable());
        }
        return old;
    }

    public static CommentOutDto mapCommentToDto(Comment comment) {
        return CommentOutDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
