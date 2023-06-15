package ru.practicum.shareit.item.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.services.ItemService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final String headerUserId = "X-Sharer-User-Id";

    private final ItemService service;


    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    @Validated
    public List<ItemOutDtoForGet> findAllByUserId(@RequestHeader(value = headerUserId)
                                                  long userId) {
        List<ItemOutDtoForGet> allItem = service.findAllByUserId(userId);
        log.info("Получены все предметы для пользователя с id {}.", userId);
        return allItem;
    }

    @GetMapping("/{itemId}")
    public ItemOutDtoForGet findById(@PathVariable
                                     long itemId,
                                     @RequestHeader(value = headerUserId)
                                     long userId) {
        ItemOutDtoForGet itemOutDtoForGet = service.findById(itemId, userId);
        log.info("Получен предмет {}", itemOutDtoForGet);
        return itemOutDtoForGet;
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDto addComment(@RequestBody CommentInDto commentInDto,
                                    @PathVariable
                                    long itemId,
                                    @RequestHeader(value = headerUserId)
                                    long userId) {
        CommentOutDto result = service.addComment(userId, itemId, commentInDto);
        log.info("Создан комментарий {}", result);
        return result;
    }

    @GetMapping("/search")
    public List<ItemOutDto> search(@RequestParam String text) {
        List<ItemOutDto> items = service.search(text);
        log.info("Получен список предметов по поиску: {}", text);
        return items;
    }

    @PostMapping
    public ItemOutDto createItem(
            @RequestBody ItemInDto itemInDto,
            @RequestHeader(value = headerUserId)
            long userId) {
        ItemOutDto createItem = service.create(itemInDto, userId);
        log.info("Добавлен предмет в базу: {}", createItem);
        return createItem;
    }

    @PatchMapping("/{itemId}")
    public ItemOutDto updateItem(@RequestBody ItemInDto itemInDto,
                                 @RequestHeader(value = headerUserId)
                                 long userId,
                                 @PathVariable long itemId) {
        ItemOutDto updateItem = service.update(itemInDto, itemId, userId);
        log.info("Обновлен предмет: {}", updateItem);
        return updateItem;
    }

    @DeleteMapping("/reset")
    public void resetDb() {
        service.resetDb();
    }
}
