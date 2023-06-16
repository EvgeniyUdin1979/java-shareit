package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.config.CustomLocaleMessenger;
import ru.practicum.shareit.item.dto.CommentInDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ItemClient extends BaseClient {
    private final CustomLocaleMessenger messenger;
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, CustomLocaleMessenger messenger) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.messenger = messenger;
    }


    public ResponseEntity<Object> findAllByUserId(long userId, int from, int size) {
        return get("?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> findById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentInDto commentInDto) {
        return post("/" + itemId + "/comment", userId, commentInDto);
    }

    public ResponseEntity<Object> search(String text, int from, int size) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ResponseEntity<>(List.of(), defaultHeaders(null), HttpStatus.OK);
        }
        return get("/search?text={text}&from={from}&size={size}", null, Map.of("text", text, "from", from, "size", size));
    }

    public ResponseEntity<Object> createItem(long userId, ItemInDto itemInDto) {
        return post("", userId, itemInDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemInDto itemInDto) {
        return patch("/" + itemId, userId, itemInDto);
    }

    public ResponseEntity<Object> resetDb() {
        return delete("/reset");
    }
}
