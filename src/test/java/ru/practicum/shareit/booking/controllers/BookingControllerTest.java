package ru.practicum.shareit.booking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/data/booking/initBooking.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "file:src/test/resources/data/booking/resetDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookingControllerTest {
    private final String itemURL = "http://localhost:8080/items";
    private final String userURL = "http://localhost:8080/users";
    private final String bookingURL = "http://localhost:8080/bookings";

    private final MockMvc mockMvc;
    ObjectMapper objectMapper;

    @Autowired
    BookingControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        javaTimeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .modules(javaTimeModule)
                .build();
    }

    //    YYYY-MM-DDTHH:mm:ss
    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/createBooking.csv", delimiter = '|')
    public void createBookingTest(String jsonCreate) throws Exception {
        BookingInDto dto = objectMapper.readValue(jsonCreate, BookingInDto.class);
        dto.setStart(LocalDateTime.now().plusMinutes(dto.getStart().getMinute()));
        dto.setEnd(LocalDateTime.now().plusMinutes(dto.getEnd().getMinute()));

        mockMvc.perform(post(bookingURL)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(3),
                        jsonPath("$.item.id").value(3),
                        jsonPath("$.booker.id").value(1),
                        jsonPath("$.status").value(Status.WAITING.name())
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/changeAvailableCreateBookingFailTest.csv", delimiter = '|')
    public void changeAvailableCreateBookingFailTest(String jsonPost, String jsonCreate, String expectedResponse) throws Exception {
        mockMvc.perform(patch(itemURL + "/3")
                        .content(jsonPost)
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        BookingInDto dto = objectMapper.readValue(jsonCreate, BookingInDto.class);
        dto.setStart(LocalDateTime.now().plusMinutes(dto.getStart().getMinute()));
        dto.setEnd(LocalDateTime.now().plusMinutes(dto.getEnd().getMinute()));
        mockMvc.perform(post(bookingURL)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/CreateBookingFailTest.csv", delimiter = '|')
    public void createBookingFailTest(String userId,
                                      String jsonCreate,
                                      boolean plus,
                                      int code,
                                      String expectedResponse) throws Exception {
        BookingInDto dto = objectMapper.readValue(jsonCreate, BookingInDto.class);

        if (plus) {
            if (dto.getStart() != null) {
                dto.setStart(LocalDateTime.now().plusMinutes(dto.getStart().getMinute()));
            }
            if (dto.getEnd() != null) {
                dto.setEnd(LocalDateTime.now().plusMinutes(dto.getEnd().getMinute()));
            }
        } else {
            if (dto.getStart() != null) {
                dto.setStart(LocalDateTime.now().minusMinutes(dto.getStart().getMinute()));
            }
            if (dto.getEnd() != null) {
                dto.setEnd(LocalDateTime.now().minusMonths(dto.getEnd().getMinute()));
            }
        }

        mockMvc.perform(post(bookingURL)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/getBooking2OwnerOrBooker.csv", delimiter = '|')
    public void getBooking2OwnerOrBooker(String userId) throws Exception {
        mockMvc.perform(get(bookingURL + "/1")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpectAll(
                        status().is(200),
                        jsonPath("$.id").value(1),
                        jsonPath("$.item.id").value(1),
                        jsonPath("$.booker.id").value(2)
                ).andReturn().getResponse().getContentAsString();

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/getBookingOwnerOrBookerFail.csv", delimiter = '|')
    public void getBookingOwnerOrBookerFail(String userId,
                                            String url,
                                            int code,
                                            String expectedResponse) throws Exception {
        mockMvc.perform(get(bookingURL + url)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/getAllBookingBookerPlusState.csv", delimiter = '|')
    public void getAllBookingBookerPlusState(String userId,
                                             String state,
                                             String jsonCreate,
                                             int arg,
                                             int id,
                                             int itemId,
                                             int bookerId) throws Exception {
        BookingInDto dto = objectMapper.readValue(jsonCreate, BookingInDto.class);
        dto.setStart(LocalDateTime.now().plusHours(dto.getStart().getHour()));
        dto.setEnd(LocalDateTime.now().plusHours(dto.getEnd().getHour()));
        MockHttpServletRequestBuilder builder;
        mockMvc.perform(post(bookingURL)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        if (state.isBlank()) {
            builder = get(bookingURL)
                    .header("X-Sharer-User-Id", userId);
        } else {
            builder = get(bookingURL)
                    .header("X-Sharer-User-Id", userId)
                    .param("state", state);
        }

        if (state.equalsIgnoreCase("future")) {
            Thread.currentThread().join(5000);
        }

        mockMvc.perform(builder)
                .andDo(print())
                .andExpectAll(
                        status().is(200),
                        jsonPath("$[%d].id", arg).value(id),
                        jsonPath("$[%d].item.id", arg).value(itemId),
                        jsonPath("$[%d].booker.id", arg).value(bookerId)

                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/getAllBookingOwnerPlusState.csv", delimiter = '|')
    public void getAllBookingOwnerPlusState(String userId,
                                            String state,
                                            int arg,
                                            int id,
                                            int itemId,
                                            int bookerId) throws Exception {
        BookingInDto dto = new BookingInDto();
        dto.setItemId(2);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(2));
        mockMvc.perform(post(bookingURL)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        MockHttpServletRequestBuilder builder;
        if (state.isBlank()) {
            builder = get(bookingURL + "/owner")
                    .header("X-Sharer-User-Id", userId);
        } else {
            builder = get(bookingURL + "/owner")
                    .header("X-Sharer-User-Id", userId)
                    .param("state", state);
        }
        if (state.equalsIgnoreCase("future")) {
            Thread.currentThread().join(5000);
        }

        mockMvc.perform(builder)
                .andDo(print())
                .andExpectAll(
                        status().is(200),
                        jsonPath("$[%d].id", arg).value(id),
                        jsonPath("$[%d].item.id", arg).value(itemId),
                        jsonPath("$[%d].booker.id", arg).value(bookerId)
                );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/booking/changeStatusFail.csv", delimiter = '|')
    public void changeStatusFail(int userId,
                                 String url,
                                 boolean approved,
                                 int code,
                                 String expectedResponse) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (approved) {
            builder = patch(bookingURL + url)
                    .header("X-Sharer-User-Id", userId)
                    .param("approved", "true");
        } else {
            builder = patch(bookingURL + "/1")
                    .header("X-Sharer-User-Id", 1);
        }

        mockMvc.perform(builder)
                .andDo(print())
                .andExpectAll(
                        status().is(code),
                        jsonPath("$.message").value(expectedResponse)
                );
    }

    @Test
    public void changeStatusFailAfterApproved() throws Exception {
        mockMvc.perform(patch(bookingURL + "/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andDo(print())
                .andExpectAll(
                        status().is(200)
                );
        mockMvc.perform(patch(bookingURL + "/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andDo(print())
                .andExpectAll(
                        status().is(400),
                        jsonPath("$.message").value("Статус бронирования уже изменен на: APPROVED.")
                );
    }

    @Test
    void getItemWithCommentsEmptyTest() throws Exception {
        mockMvc.perform(get(itemURL + "/1")
                        .header("X-Sharer-User-Id", "1")
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.comments.length()").value(0)
                );
    }

    @Test
    void postCommentWithoutBookingFailTest() throws Exception {
        mockMvc.perform(post(itemURL + "/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Add comment from user1\"}")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("У пользователя с id: 1 нет подтвержденных завершенных бронирований на предмет с id: 1.")
                );
    }

    @Test
    void postCommentNotPastBookingFailTest() throws Exception {
        mockMvc.perform(patch(bookingURL + "/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post(itemURL + "/1/comment")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Add comment from user2\"}")
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("У пользователя с id: 2 нет подтвержденных завершенных бронирований на предмет с id: 1.")
                );
    }

    @Test
    void postCommentWithBooking() throws Exception {
        mockMvc.perform(patch(bookingURL + "/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                )
                .andDo(print())
                .andExpect(status().isOk());
        Thread.currentThread().join(5000);

        mockMvc.perform(post(itemURL + "/1/comment")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Add comment from user2\"}")
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.text").value("Add comment from user2"),
                        jsonPath("$.authorName").value("Petia")
                );
    }
}