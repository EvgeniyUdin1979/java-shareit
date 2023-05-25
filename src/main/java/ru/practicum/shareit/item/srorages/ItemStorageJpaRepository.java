package ru.practicum.shareit.item.srorages;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorageJpaRepository extends JpaRepository<Item, Long> {
    @Transactional
    @Query(value = "Select * from (select * from Items it1 where it1.available = true) it2 where it2.name ilike %:text% or it2.description ilike %:text% ", nativeQuery = true)
    List<Item> findAllByNameOrDescriptionContainsIgnoreCase(String text);

    @Transactional
    List<Item> findAllByOwnerId(long userId, Sort sort);

//    Comment addComment(long userId, long itemId, String text);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comments;ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;" +
            "DELETE FROM items;ALTER TABLE items ALTER COLUMN id RESTART WITH 1;", nativeQuery = true)
    void clearDb();
}
