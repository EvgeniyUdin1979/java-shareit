package ru.practicum.shareit.request.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.advice.CustomAdvice;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.srorages.dao.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.services.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storages.dao.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storages.dao.UserStorage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemRequestController.class, ItemRequestServiceImpl.class, CustomAdvice.class})
@ExtendWith(SpringExtension.class)
class ItemRequestControllerTest {

    private final String itemRequestURL = "http://localhost:8080/requests";


    @MockBean
    private ItemRequestStorage requestStorage;

    @MockBean
    private UserStorage userStorage;

    @MockBean
    private ItemStorage itemStorage;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void addTest() throws Exception {
        Mockito.when(userStorage.existsId(1)).thenReturn(true);
        Mockito.when(userStorage.findUserById(1))
                .thenReturn(
                        User.builder()
                                .id(1)
                                .name("user1")
                                .email("user1@user.ru")
                                .build());
        Mockito.when(requestStorage.create(any(ItemRequest.class))).thenReturn(
                ItemRequest.builder()
                        .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                        .id(1)
                        .description("Хотел бы воспользоваться щёткой для обуви")
                        .requestor(User.builder()
                                .id(1)
                                .name("user1")
                                .email("user1@user.ru")
                                .build())
                        .build());
        Mockito.when(requestStorage.findAllItemIdByRequestId(1)).thenReturn(new ArrayList<>());

        mockMvc.perform(post(itemRequestURL)
                        .header("X-Sharer-User-Id", 1)
                        .content("{ \"description\": \"Хотел бы воспользоваться щёткой для обуви\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"),
                        jsonPath("$.created").value(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()),
                        jsonPath("$.items.length()").value(0)
                );

    }

    @Test
    void getAllByRequestorTest() throws Exception {
        Mockito.when(userStorage.existsId(1)).thenReturn(true);
        Mockito.when(requestStorage.findAllByRequestorId(1)).thenReturn(List.of(ItemRequest.builder()
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .id(1)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .requestor(User.builder()
                        .id(1)
                        .name("user1")
                        .email("user1@user.ru")
                        .build())
                .build()));
        mockMvc.perform(get(itemRequestURL)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].description").value("Хотел бы воспользоваться щёткой для обуви"),
                        jsonPath("$[0].created").value(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()),
                        jsonPath("$[0].items.length()").value(0));
    }

    @Test
    void getAllTest() throws Exception {
        User user1 = User.builder()
                .id(1)
                .name("user1")
                .email("user1@user.ru")
                .build();
        Item item = Item.builder()
                .id(1)
                .requestId(1L)
                .owner(user1)
                .available(true)
                .description("Описание предмета1")
                .name("предмет1")
                .build();

        Mockito.when(userStorage.existsId(anyLong())).thenReturn(true);
        Mockito.when(requestStorage.findAll(anyInt(), anyInt(), anyLong()))
                .thenAnswer(i -> {
                    if ((Long) i.getArgument(2) != user1.getId()) {
                        return List.of(ItemRequest.builder()
                                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                .id(1)
                                .description("Хотел бы воспользоваться щёткой для обуви")
                                .requestor(user1)
                                .build());
                    }
                    return List.of();
                });
        Mockito.when(requestStorage.findAllItemIdByRequestId(anyLong()))
                .thenReturn(List.of(item));


        mockMvc.perform(get(itemRequestURL + "/all")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].description").value("Хотел бы воспользоваться щёткой для обуви"),
                        jsonPath("$[0].created").value(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()),
                        jsonPath("$[0].items.length()").value(1),
                        jsonPath("$[0].items[0].name").value("предмет1"));
    }

    @Test
    void getByRequestTest() throws Exception {
        User user1 = User.builder()
                .id(1)
                .name("user1")
                .email("user1@user.ru")
                .build();
        Item item = Item.builder()
                .id(1)
                .requestId(1L)
                .owner(user1)
                .available(true)
                .description("Описание предмета1")
                .name("предмет1")
                .build();

        Mockito.when(userStorage.existsId(anyLong())).thenReturn(true);
        Mockito.when(requestStorage.isExists(anyLong())).thenReturn(true);
        Mockito.when(requestStorage.findById(anyLong())).thenReturn(ItemRequest.builder()
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .id(1)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .requestor(user1)
                .build());
        Mockito.when(requestStorage.findAllItemIdByRequestId(anyLong()))
                .thenReturn(List.of(item));

        mockMvc.perform(get(itemRequestURL + "/1")
                        .header("X-Sharer-User-Id", 1)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"),
                        jsonPath("$.created").value(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()),
                        jsonPath("$.items.length()").value(1),
                        jsonPath("$.items[0].name").value("предмет1"));

    }

    @Test
    void resetDbTest() throws Exception {
        mockMvc.perform(delete(itemRequestURL + "/reset"))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(requestStorage, Mockito.times(1)).resetDb();
    }


}