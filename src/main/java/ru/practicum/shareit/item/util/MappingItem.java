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
                .requestId(itemInDto.getRequestId())
                .build();
    }

    public static ItemOutDto mapToDto(Item item) {
        ItemOutDto dto = ItemOutDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequestId() != null) {
            dto.setRequestId(item.getRequestId());
        }
        return dto;
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
        Item newest = Item.builder()
                .id(old.getId())
                .owner(old.getOwner())
                .comments(old.getComments())
                .requestId(newestDto.getRequestId())
                .build();
        if (newestDto.getName() != null) {
            newest.setName(newestDto.getName());
        } else {
            newest.setName(old.getName());
        }
        if (newestDto.getDescription() != null) {
            newest.setDescription(newestDto.getDescription());
        } else {
            newest.setDescription(old.getDescription());
        }
        if (newestDto.getAvailable() != null) {
            newest.setAvailable(newestDto.getAvailable());
        } else {
            newest.setAvailable(old.getAvailable());
        }
        return newest;
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
