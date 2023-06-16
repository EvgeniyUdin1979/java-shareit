package ru.practicum.shareit.request.storages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Sql(scripts = "file:src/test/resources/data/itemrequest/initRequest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "file:src/test/resources/data/itemrequest/resetDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ItemRequestStorageImplTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestStorageImpl storage;

    @BeforeEach
    public void createRequests() {
        User user = em.find(User.class, 1L);
        ItemRequest itemRequest1 = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("itemRequest1")
                .requestor(user)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .created(LocalDateTime.now().plusMinutes(1))
                .description("itemRequest2")
                .requestor(user)
                .build();
        storage.create(itemRequest1);
        storage.create(itemRequest2);
    }

    @Test
    void create() {
        ItemRequest result = storage.findById(1L);
        assertThat(result, hasProperty("id", is(1L)));
        assertThat(result, hasProperty("description", equalTo("itemRequest1")));
        assertThat(result, hasProperty("requestor", hasProperty("id", is(1L))));
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> result = storage.findAllByRequestorId(1L);
        assertThat(result, hasSize(2));
        assertThat(result.get(0), hasProperty("description", equalTo("itemRequest2")));
        assertThat(result.get(0), hasProperty("id", equalTo(2L)));
        assertThat(result.get(0), hasProperty("requestor", hasProperty("id", equalTo(1L))));
        assertThat(result.get(1), hasProperty("id", equalTo(1L)));
        assertThat(result.get(1), hasProperty("requestor", hasProperty("id", equalTo(1L))));
    }

    @Test
    void findAll() {
        List<ItemRequest> result = storage.findAll(0, 2, 2);
        assertThat(result, hasSize(2));
        assertThat(result.get(0), hasProperty("description", equalTo("itemRequest2")));
        assertThat(result.get(0), hasProperty("id", equalTo(2L)));
        assertThat(result.get(0), hasProperty("requestor", hasProperty("id", equalTo(1L))));
        assertThat(result.get(1), hasProperty("id", equalTo(1L)));
        assertThat(result.get(1), hasProperty("requestor", hasProperty("id", equalTo(1L))));

        List<ItemRequest> resultPage1 = storage.findAll(0, 1, 2);
        assertThat(resultPage1, hasSize(1));
        assertThat(resultPage1.get(0), hasProperty("description", equalTo("itemRequest2")));
        assertThat(resultPage1.get(0), hasProperty("id", equalTo(2L)));
        assertThat(resultPage1.get(0), hasProperty("requestor", hasProperty("id", equalTo(1L))));

        List<ItemRequest> resultPage2 = storage.findAll(1, 1, 2);
        assertThat(resultPage2, hasSize(1));
        assertThat(resultPage2.get(0), hasProperty("description", equalTo("itemRequest1")));
        assertThat(resultPage2.get(0), hasProperty("id", equalTo(1L)));
        assertThat(resultPage2.get(0), hasProperty("requestor", hasProperty("id", equalTo(1L))));

    }


}