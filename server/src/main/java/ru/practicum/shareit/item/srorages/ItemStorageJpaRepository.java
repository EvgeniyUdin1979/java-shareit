package ru.practicum.shareit.item.srorages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorageJpaRepository extends JpaRepository<Item, Long> {
    @Transactional
    @Query(value = "Select * from (select * from Items it1 where it1.available = true) it2 where it2.name ilike %:text% or it2.description ilike %:text% ", nativeQuery = true)
    Page<Item> findAllByNameOrDescriptionContainsIgnoreCase(String text, Pageable pageable);

    @Transactional
    @Query(value = "select * from Items it join users u on it.owner_id = u.id where u.id = ?1 order by it.id asc", nativeQuery = true)
    Page<Item> findAllByOwnerId(long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comments;ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;" +
            "DELETE FROM items;ALTER TABLE items ALTER COLUMN id RESTART WITH 1;", nativeQuery = true)
    void clearDb();
}
