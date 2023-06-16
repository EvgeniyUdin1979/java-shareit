package ru.practicum.shareit.request.storages;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestJpaRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(long requestorId, Sort sort);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM requests;ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;", nativeQuery = true)
    void clearDb();

}
