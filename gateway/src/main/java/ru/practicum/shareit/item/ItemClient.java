package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentInDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> findAllByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentInDto commentInDto) {
        return post("/" + itemId + "/comment", userId, commentInDto);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text={text}", null, Map.of("text", text));
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
