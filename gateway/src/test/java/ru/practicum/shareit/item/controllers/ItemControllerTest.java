package ru.practicum.shareit.item.controllers;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.InetSocketAddress;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    private final String itemURL = "http://localhost:8080/items";

    private final String userURL = "http://localhost:8080/users";


    protected static HttpServer server;

    @Autowired
    private MockMvc mockMvc;

    @AfterAll
    protected static void stop() {
        server.stop(0);
    }

    @BeforeAll
    protected static void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(9090), 0);
        server.createContext("/", e -> {
            e.sendResponseHeaders(200, 0);
            e.close();
        });
        server.setExecutor(null);
        server.start();
    }

    @Test
    void findAllByUserId() throws Exception {
        mockMvc.perform(get(itemURL)
                        .header("X-Sharer-User-Id", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
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
        mockMvc.perform(get(itemURL)
                        .header(headerName, userId)
                )
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get(itemURL + "/1")
                        .header("X-Sharer-User-Id", "1")
                )
                .andDo(print())
                .andExpectAll(status().isOk());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/findByIdFail.csv", delimiter = '|')
    void findByIdFail(String url,
                      String expectedResponse,
                      String user,
                      String item,
                      int code) throws Exception {
        mockMvc.perform(get(itemURL + url)
                        .header("X-Sharer-User-Id", "1")

                )
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @Test
    void search() throws Exception {
        mockMvc.perform(get(itemURL + "/search")
                        .param("text", "text")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/item/searchEmpty.csv", delimiter = '|')
    void searchEmpty(String text, int expectedLength, String user, String item1, String item2) throws Exception {
        mockMvc.perform(get(itemURL + "/search")
                        .param("text", text)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(expectedLength)
                );
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(patch(itemURL + "/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Аккумуляторная дрель\",\"description\": \"Простая дрель\",\"available\": true}")
                )
                .andDo(print())
                .andExpect(status().isOk());
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