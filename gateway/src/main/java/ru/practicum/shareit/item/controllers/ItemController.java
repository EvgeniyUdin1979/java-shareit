package ru.practicum.shareit.item.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentInDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@Validated
@RequestMapping("/items")
public class ItemController {
    private final String headerUserId = "X-Sharer-User-Id";

    private final ItemClient client;

    @Autowired
    public ItemController(ItemClient client) {
        this.client = client;
    }

    @GetMapping
    @Validated
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(value = headerUserId)
                                                  @Positive(message = "{item.controller.userIdNotPositive}")
                                                  long userId,
                                                  @PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                                  @RequestParam(name = "from", defaultValue = "0") int from,
                                                  @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        return client.findAllByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable
                                           @Positive(message = "{item.controller.itemIdNotPositive}") long itemId,
                                           @RequestHeader(value = headerUserId)
                                           @Positive(message = "{item.controller.userIdNotPositive}")
                                           long userId) {
        return client.findById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentInDto commentInDto,
                                             @PathVariable
                                             @Positive(message = "{item.controller.itemIdNotPositive}") long itemId,
                                             @RequestHeader(value = headerUserId)
                                             @Positive(message = "{item.controller.userIdNotPositive}")
                                             long userId) {
        return client.addComment(userId, itemId, commentInDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        return client.search(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(value = Create.class)
                                             @RequestBody ItemInDto itemInDto,
                                             @RequestHeader(value = headerUserId)
                                             @Positive(message = "{item.controller.userIdNotPositive}")
                                             long userId) {
        return client.createItem(userId, itemInDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(value = Update.class)
                                             @RequestBody ItemInDto itemInDto,
                                             @RequestHeader(value = headerUserId)
                                             @Positive(message = "{item.controller.userIdNotPositive}")
                                             long userId,
                                             @PathVariable
                                             @Positive(message = "{item.controller.itemIdNotPositive}") long itemId) {
        return client.updateItem(userId, itemId, itemInDto);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Object> resetDb() {
        return client.resetDb();
    }
}
