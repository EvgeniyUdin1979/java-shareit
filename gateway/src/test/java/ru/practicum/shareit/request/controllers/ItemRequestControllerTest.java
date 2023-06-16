package ru.practicum.shareit.request.controllers;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    String currentTestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    private final String headerUserId = "X-Sharer-User-Id";

    private final String itemRequestURL = "http://localhost:8080/requests";

    @Autowired
    private MockMvc mockMvc;

    protected static HttpServer server;

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
    void addTest() throws Exception {
        mockMvc.perform(post(itemRequestURL)
                .header(headerUserId, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"description\": \"Хотел бы воспользоваться щёткой для обуви\"\n" +
                        "}")
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void addFailTest() throws Exception {
        mockMvc.perform(post(itemRequestURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"description\": \"Хотел бы воспользоваться щёткой для обуви\"\n" +
                        "}")
        ).andDo(print()).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("Отсутствует указание пользователя для данного запроса.")
        );

        mockMvc.perform(post(itemRequestURL)
                .header(headerUserId, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        ).andDo(print()).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("Описание не может отсутствовать или быть пустым.")
        );

        mockMvc.perform(post(itemRequestURL)
                .header(headerUserId, "1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("Отсутствует тело(json) для данного запроса.")
        );
    }

    @Test
    void getAllByRequestorTest() throws Exception {
        mockMvc.perform(get(itemRequestURL)
                        .header(headerUserId, "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getAllByRequestorFailTest() throws Exception {
        mockMvc.perform(get(itemRequestURL)
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Отсутствует указание пользователя для данного запроса.")
                );
    }

    @Test
    void getAllTest() throws Exception {
        mockMvc.perform(get(itemRequestURL + "/all")
                        .header(headerUserId, "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getAllFailUserIdTest() throws Exception {
        mockMvc.perform(get(itemRequestURL + "/all")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Отсутствует указание пользователя для данного запроса.")
                );

        mockMvc.perform(get(itemRequestURL + "/all")
                        .header(headerUserId, "a")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Параметр X-Sharer-User-Id не является числом.")
                );

        mockMvc.perform(get(itemRequestURL + "/all")
                        .header(headerUserId, "-1")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("id пользователя не может быть отрицательным или равен 0.")
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/itemrequest/getAllFailFromAndSizeTest.csv", delimiter = '|')
    void getAllFailFromAndSizeTest(
            String from,
            String size,
            String expectedResponse,
            int code
    ) throws Exception {
        mockMvc.perform(get(itemRequestURL + "/all")
                        .header(headerUserId, "1")
                        .param("from", from)
                        .param("size", size)
                )
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @Test
    void getRequestByIdTest() throws Exception {
        mockMvc.perform(get(itemRequestURL + "/1")
                        .header(headerUserId, "1")
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void getRequestByIdFailUserIdTest() throws Exception {
        mockMvc.perform(get(itemRequestURL + "/1")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Отсутствует указание пользователя для данного запроса.")
                );

        mockMvc.perform(get(itemRequestURL + "/1")
                        .header(headerUserId, "a")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Параметр X-Sharer-User-Id не является числом.")
                );

        mockMvc.perform(get(itemRequestURL + "/1")
                        .header(headerUserId, "-1")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("id пользователя не может быть отрицательным или равен 0.")
                );
    }

    @Test
    void getRequestByIdFailRequestIdTest() throws Exception {
        mockMvc.perform(get(itemRequestURL + "/-1")
                        .header(headerUserId, "1")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Параметр id запроса на бронирование не может быть отрицательным или равен 0.")
                );

        mockMvc.perform(get(itemRequestURL + "/a")
                        .header(headerUserId, "1")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Параметр requestId не является числом.")
                );


    }
}