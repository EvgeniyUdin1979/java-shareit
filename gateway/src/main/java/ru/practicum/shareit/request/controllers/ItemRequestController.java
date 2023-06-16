package ru.practicum.shareit.request.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestInDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final String headerUserId = "X-Sharer-User-Id";

    private final RequestClient client;

    @Autowired
    public ItemRequestController(RequestClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<Object> add(@Valid
                                      @RequestBody ItemRequestInDto itemRequestInDto,
                                      @RequestHeader(value = headerUserId)
                                      @Positive(message = "{item.controller.userIdNotPositive}")
                                      long userId) {
        return client.addRequest(userId, itemRequestInDto);

    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestor(@RequestHeader(value = headerUserId)
                                                    @Positive(message = "{item.controller.userIdNotPositive}")
                                                    long userId) {
        return client.getAllByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                         @RequestParam(name = "size", defaultValue = "10") int size,
                                         @RequestHeader(value = headerUserId)
                                             @Positive(message = "{item.controller.userIdNotPositive}")
                                         long userId) {
        return client.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable
                                               @Positive(message = "{itemRequest.controller.itemRequestIdNotPositive}") long requestId,
                                               @RequestHeader(value = headerUserId)
                                               @Positive(message = "{item.controller.userIdNotPositive}")
                                               long userId) {
        return client.getByRequest(userId, requestId);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Object> resetDb() {
        return client.resetDb();
    }


}
