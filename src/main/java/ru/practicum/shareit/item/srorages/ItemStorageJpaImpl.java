package ru.practicum.shareit.item.srorages;

import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QComment;
import ru.practicum.shareit.item.srorages.dao.CommentRepository;
import ru.practicum.shareit.item.srorages.dao.ItemStorage;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Repository(value = "itemJpa")
public class ItemStorageJpaImpl implements ItemStorage, CommentRepository {

    private final ItemStorageJpaRepository repository;
    private final EntityManager em;

    private final DataSource dataSource;

    @Autowired
    public ItemStorageJpaImpl(ItemStorageJpaRepository repository, EntityManager em, DataSource dataSource) {
        this.repository = repository;
        this.em = em;
        this.dataSource = dataSource;
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return repository.findAllByOwnerId(userId, Sort.by("id").ascending());
    }

    @Override
    public Item findItemById(long id) {
        return repository.findById(id).get();
    }

    @Override
    public List<Item> search(String text) {
        return repository.findAllByNameOrDescriptionContainsIgnoreCase(text);
    }

    @Override
    public Item create(Item item) {
        return repository.save(item);
    }

    @Override
    public Item update(Item item) {
        return repository.save(item);
    }

    @Override
    public boolean existsId(long id) {
        return repository.existsById(id);
    }

    @Override
    public Booking findLastBooking(long itemId, long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QBooking booking = QBooking.booking;
        DateExpression<LocalDateTime> date = Expressions.asDate(LocalDateTime.now());
        return queryFactory.selectFrom(booking)
                .where(booking.status.eq(Status.APPROVED))
                .where(booking.item.owner.id.eq(userId))
                .where(booking.item.id.eq(itemId))
                .where(date.after(booking.startDate))
                .orderBy(booking.endDate.desc())
                .fetchFirst();
    }

    @Override
    public Booking findNextBooking(long itemId, long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QBooking booking = QBooking.booking;
        DateExpression<LocalDateTime> date = Expressions.asDate(LocalDateTime.now());
        return queryFactory.selectFrom(booking)
                .where(booking.status.eq(Status.APPROVED))
                .where(booking.item.owner.id.eq(userId))
                .where(booking.item.id.eq(itemId))
                .where(date.before(booking.startDate))
                .orderBy(booking.endDate.asc())
                .fetchFirst();


    }

    @Override
    public Comment add(long userId, long itemId, String text) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("text", text)
                .addValue("item_id", itemId)
                .addValue("author_id", userId)
                .addValue("created", LocalDateTime.now());

        long commentId = (Long) new SimpleJdbcInsert(dataSource)
                .withTableName("Comments")
                .usingGeneratedKeyColumns("id").executeAndReturnKey(param);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QComment comment = QComment.comment;

        return queryFactory.selectFrom(comment).where(comment.id.eq(commentId)).fetchOne();
    }

    @Override
    public void resetDb() {
        repository.clearDb();
    }
}
