package ru.practicum.shareit.user.controllers;

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
class UserControllerTest {

    private final String baseURL = "http://localhost:8080/users";
    static HttpServer server;

    @Autowired
    MockMvc mockMvc;

    @AfterAll
    static void stop() {
        server.stop(0);
    }

    @BeforeAll
    static void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(9090), 0);
        server.createContext("/", e -> {
            e.sendResponseHeaders(200, 0);
            e.close();
        });
        server.setExecutor(null);
        server.start();
    }


    @Test
    void findAll() throws Exception {
        mockMvc.perform(get(baseURL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get(baseURL + "/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/createFail.csv", delimiter = '|')
    void createFail(String expectedResponse, String json, String failJson, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                        .content(failJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().is(code), jsonPath("$.message").value(expectedResponse));
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete(baseURL + "/1"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/deleteFail.csv", delimiter = '|')
    void deleteFail(String expectedResponse, String json, String url, int code) throws Exception {
        mockMvc.perform(delete(baseURL + url))
                .andDo(print())
                .andExpectAll(status().is(code), jsonPath("$.message").value(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/create.csv", delimiter = '|')
    void create(String json, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().is(code));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/findByIdFail.csv", delimiter = '|')
    void findByIdFail(String url, String expectedResponse, String json, int code) throws Exception {
        mockMvc.perform(get(baseURL + url))
                .andDo(print())
                .andExpectAll(status().is(code), jsonPath("$.message").value(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/update.csv", delimiter = '|')
    void update(String url, String json, String updateJson, String expectedJson, int code) throws Exception {
        mockMvc.perform(patch(baseURL + url)
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is(code));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/updateFail.csv", delimiter = '|')
    void updateFail(String url, String expectedResponse, String json1, String json2, String updateJson, int code) throws Exception {
        mockMvc.perform(patch(baseURL + url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andDo(print())
                .andExpectAll(status().is(code), jsonPath("$.message").value(expectedResponse));
    }
}