package ru.practicum.shareit.user.controllers;

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
class UserControllerTest {

    private final String baseURL = "http://localhost:9090/users";

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void reset() throws Exception {
        mockMvc.perform(delete(baseURL + "/reset"));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/findall.csv", delimiter = '|')
    void findAll(String json1, String json2) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json1)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(baseURL)
                .content(json2)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(baseURL))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].name").value("user1"),
                        jsonPath("$[1].name").value("user2")
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/findById.csv", delimiter = '|')
    void findById(String url, String json, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(baseURL + url))
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("user1")
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/createFail.csv", delimiter = '|')
    void createFail(String expectedResponse, String json, String failJson, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(baseURL)
                        .content(failJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().is(code), jsonPath("$.message").value(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/deleteById.csv", delimiter = '|')
    void deleteById(String url, String json, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(delete(baseURL + url))
                .andExpect(status().is(code));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/deleteFail.csv", delimiter = '|')
    void deleteFail(String expectedResponse, String json, String url, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

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
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("user1")
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/findByIdFail.csv", delimiter = '|')
    void findByIdFail(String url, String expectedResponse, String json, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(baseURL + url))
                .andDo(print())
                .andExpectAll(status().is(code), jsonPath("$.message").value(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/update.csv", delimiter = '|')
    void update(String url, String json, String updateJson, String expectedJson, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(patch(baseURL + url)
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpectAll(
                        status().is(code),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(expectedJson)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/user/updateFail.csv", delimiter = '|')
    void updateFail(String url, String expectedResponse, String json1, String json2, String updateJson, int code) throws Exception {
        mockMvc.perform(post(baseURL)
                .content(json1)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post(baseURL)
                .content(json2)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(patch(baseURL + url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andDo(print())
                .andExpectAll(status().is(code), jsonPath("$.message").value(expectedResponse));
    }
}