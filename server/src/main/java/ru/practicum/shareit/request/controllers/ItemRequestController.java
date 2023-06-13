package ru.practicum.shareit.request.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestOutDto add(@Valid
                                 @RequestBody ItemRequestInDto itemRequestInDto,
                                 @RequestHeader(value = "X-Sharer-User-Id")
                                 @Positive(message = "{item.controller.userIdNotPositive}")
                                 long userId) {
        ItemRequestOutDto result = service.create(itemRequestInDto, userId);
        log.info("Создан запрос: {}", result);
        return result;

    }

    @GetMapping
    public List<ItemRequestOutDto> getAllByRequestor(@RequestHeader(value = "X-Sharer-User-Id")
                                                     @Positive(message = "{item.controller.userIdNotPositive}")
                                                     long userId) {
        List<ItemRequestOutDto> result = service.findAllByRequestor(userId);
        log.info("Получен список запросов для пользователя с id: {}", userId);
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAll(@PositiveOrZero(message = "{itemRequest.controller.fromNotPositiveOrZero}")
                                          @RequestParam(name = "from", defaultValue = "0") int from,
                                          @Positive(message = "{itemRequest.controller.sizeNotPositive}")
                                          @RequestParam(name = "size", defaultValue = "10") int size,
                                          @RequestHeader(value = "X-Sharer-User-Id")
                                          @Positive(message = "{item.controller.userIdNotPositive}")
                                          long userId) {
        List<ItemRequestOutDto> result = service.findAll(from, size, userId);
        log.info("Получен список запросов начиная со страницы: {}, размер страницы: {}", from, size);
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getByRequest(@PathVariable
                                          @Positive(message = "{item.controller.itemIdNotPositive}") long requestId,
                                          @RequestHeader(value = "X-Sharer-User-Id")
                                          @Positive(message = "{item.controller.userIdNotPositive}")
                                          long userId) {
        ItemRequestOutDto result = service.findById(requestId, userId);
        log.info("Получен запрос с id: {}, для пользователя с id: {}", requestId, userId);
        return result;
    }

    @DeleteMapping("/reset")
    public void resetDb() {
        service.resetDb();
    }


}
