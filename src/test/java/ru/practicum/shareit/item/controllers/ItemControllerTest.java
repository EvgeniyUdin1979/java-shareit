package ru.practicum.shareit.item.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    private final String itemURL = "http://localhost:8080/items";
    private final String userURL = "http://localhost:8080/users";

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void reset() throws Exception {
        mockMvc.perform(delete(itemURL + "/reset"));
        mockMvc.perform(delete(userURL + "/reset"));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/findAllByUserId.csv", delimiter = '|')
    void findAllByUserId(String user, String item1, String item2) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item1)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item2)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(itemURL)
                        .header("X-Sharer-User-Id", "1")
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].name").value("Дрель"),
                        jsonPath("$[1].name").value("Отвертка")
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/findAllByUserIdFail.csv", delimiter = '|')
    void findAllByUserIdFail(String userId,
                             String expectedResponse,
                             String user,
                             String item1,
                             String item2,
                             String headerName,
                             int code) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item1)
                .header(headerName, "1")
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item2)
                .header(headerName, "1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(itemURL)
                        .header(headerName, userId)
                )
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/findById.csv", delimiter = '|')
    void findById(String url, String user, String item) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(itemURL + url)
                        .header("X-Sharer-User-Id", "1")

                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Дрель")
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/findByIdFail.csv", delimiter = '|')
    void findByIdFail(String url,
                      String expectedResponse,
                      String user,
                      String item,
                      int code) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(itemURL + url)
                        .header("X-Sharer-User-Id", "1")

                )
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/search.csv", delimiter = '|')
    void search(String text, String expectedResponse, int expectedLength, String user, String item1, String item2) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item1)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item2)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(itemURL + "/search")
                        .param("text", text)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(expectedLength),
                        jsonPath("$[0].name").value(expectedResponse)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/searchEmpty.csv", delimiter = '|')
    void searchEmpty(String text, int expectedLength, String user, String item1, String item2) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item1)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item2)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(itemURL + "/search")
                        .param("text", text)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(expectedLength)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/update.csv", delimiter = '|')
    void update(String url, String user, String item, String update, String expectedResponse) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(patch(itemURL + url)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponse)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/updateFail.csv", delimiter = '|')
    void updateFail(String itemId,
                    String expectedResponse,
                    String user,
                    String userId,
                    String item,
                    String update,
                    String header,
                    int code) throws Exception {
        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(itemURL)
                .content(item)
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(patch(itemURL + itemId)
                        .header(header, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update)
                )
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/createFail.csv", delimiter = '|')
    void createFail(String expectedResponse,
                    String userId,
                    String user,
                    String item,
                    String header,
                    int code) throws Exception {

        mockMvc.perform(post(userURL)
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(itemURL)
                        .header(header, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(item)
                )
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }
}