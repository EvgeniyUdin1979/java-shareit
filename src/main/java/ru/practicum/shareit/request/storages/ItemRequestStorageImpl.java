package ru.practicum.shareit.request.storages;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.QItemRequest;
import ru.practicum.shareit.request.storages.dao.ItemRequestStorage;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class ItemRequestStorageImpl implements ItemRequestStorage {

    private final ItemRequestJpaRepository repository;

    private final EntityManager em;

    public ItemRequestStorageImpl(ItemRequestJpaRepository repository, EntityManager em) {
        this.repository = repository;
        this.em = em;
    }


    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        return repository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> findAllByRequestorId(long requestorId) {
        return repository.findAllByRequestorId(requestorId, Sort.by("created").descending());
    }

    @Override
    public List<ItemRequest> findAll(int from, int size, long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItemRequest itemRequest = QItemRequest.itemRequest;
        return queryFactory.selectFrom(itemRequest)
                .where(itemRequest.requestor.id.ne(userId))
                .offset(from)
                .limit(size)
                .orderBy(itemRequest.created.desc())
                .fetch();
    }

    @Override
    public ItemRequest findById(long requestId) {
        return repository.findById(requestId).get();
    }

    public List<Item> findAllItemIdByRequestId(long requestId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem item = QItem.item;
        return queryFactory.selectFrom(item).where(item.requestId.eq(requestId)).fetch();
    }

    @Override
    public boolean isExists(long requestId) {
        return repository.existsById(requestId);
    }

    public void resetDb() {
        repository.clearDb();
    }

}
