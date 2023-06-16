package ru.practicum.shareit.request.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final String headerUserId = "X-Sharer-User-Id";

    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestOutDto add(@RequestBody ItemRequestInDto itemRequestInDto,
                                 @RequestHeader(value = headerUserId)
                                 long userId) {
        ItemRequestOutDto result = service.create(itemRequestInDto, userId);
        log.info("Создан запрос: {}", result);
        return result;

    }

    @GetMapping
    public List<ItemRequestOutDto> getAllByRequestor(@RequestHeader(value = headerUserId)
                                                     long userId) {
        List<ItemRequestOutDto> result = service.findAllByRequestor(userId);
        log.info("Получен список запросов для пользователя с id: {}", userId);
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                          @RequestParam(name = "size", defaultValue = "10") int size,
                                          @RequestHeader(value = headerUserId)
                                          long userId) {
        List<ItemRequestOutDto> result = service.findAll(from, size, userId);
        log.info("Получен список запросов начиная со страницы: {}, размер страницы: {}", from, size);
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getByRequest(@PathVariable long requestId,
                                          @RequestHeader(value = headerUserId)
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
