package ru.practicum.shareit.item.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.item.util.MappingItem;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;


    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    @Validated
    public List<ItemDto> findAllByUserId(@RequestHeader(value = "X-Sharer-User-Id")
                                         @Positive(message = "{item.controller.userIdNotPositive}")
                                         long userId) {
        List<ItemDto> allItem = service.findAllByUserId(userId);
        log.info("Получены все предметы для пользователя с id {}.", userId);
        return allItem;
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable
                            @Positive(message = "{item.controller.itemIdNotPositive}") long id) {
        ItemDto itemDto = service.findById(id);
        log.info("Получен предмет {}", itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        List<ItemDto> items = service.search(text);
        log.info("Получен список предметов по поиску: {}", text);
        return items;
    }

    @PostMapping
    public ItemDto create(@Validated(value = Create.class)
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id")
                          @Positive(message = "{item.controller.userIdNotPositive}")
                          long userId) {
        Item item = MappingItem.mapToItem(itemDto);
        item.setOwnerId(userId);
        ItemDto createItem = service.create(item);
        log.info("Добавлен предмет в базу: {}", createItem);
        return createItem;
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Validated(value = Update.class)
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id")
                          @Positive(message = "{item.controller.userIdNotPositive}")
                          long userId,
                          @PathVariable
                          @Positive(message = "{item.controller.itemIdNotPositive}") long id) {
        itemDto.setId(id);
        Item item = MappingItem.mapToItem(itemDto);
        item.setOwnerId(userId);
        ItemDto updateItem = service.update(item);
        log.info("Обновлен предмет: {}", updateItem);
        return updateItem;
    }

    @DeleteMapping("/reset")
    public void resetDb() {
        service.resetDb();
    }
}
