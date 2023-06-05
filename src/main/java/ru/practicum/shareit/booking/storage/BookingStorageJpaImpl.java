package ru.practicum.shareit.booking.storage;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.dao.BookingStorage;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BookingStorageJpaImpl implements BookingStorage {

    private final BookingStorageJpaRepository repository;

    private final EntityManager em;

    @Autowired
    public BookingStorageJpaImpl(BookingStorageJpaRepository repository, EntityManager em) {
        this.repository = repository;
        this.em = em;
    }

    @Override
    public Booking createBooking(Booking booking) {
        return repository.save(booking);
    }

    @Override
    public List<Booking> findAllBookingsForBookerOrOwner(boolean isBooker, long userId, State state, int from, int size) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QBooking booking = QBooking.booking;
        DateExpression<LocalDateTime> date = Expressions.asDate(LocalDateTime.now());
        BooleanExpression bookerOrOwner;
        if (isBooker) {
            bookerOrOwner = booking.booker.id.eq(userId);
        } else {
            bookerOrOwner = booking.item.owner.id.eq(userId);
        }
        switch (state) {
            case ALL: {
                return queryFactory.selectFrom(booking)
                        .where(bookerOrOwner)
                        .orderBy(booking.startDate.desc())
                        .limit(size)
                        .offset(from)
                        .fetch();
            }
            case WAITING: {
                return queryFactory.selectFrom(booking)
                        .where(bookerOrOwner)
                        .where(booking.status.eq(Status.WAITING))
                        .orderBy(booking.startDate.desc())
                        .limit(size)
                        .offset(from)
                        .fetch();
            }
            case REJECTED: {
                return queryFactory.selectFrom(booking)
                        .where(bookerOrOwner)
                        .where(booking.status.eq(Status.REJECTED))
                        .orderBy(booking.startDate.desc())
                        .limit(size)
                        .offset(from)
                        .fetch();
            }
            case CURRENT: {
                return queryFactory.selectFrom(booking)
                        .where(bookerOrOwner)
                        .where(date.between(booking.startDate, booking.endDate))
                        .orderBy(booking.startDate.desc())
                        .limit(size)
                        .offset(from)
                        .fetch();
            }
            case PAST: {
                return queryFactory.selectFrom(booking)
                        .where(bookerOrOwner)
                        .where(booking.endDate.before(LocalDateTime.now()))
                        .orderBy(booking.startDate.desc())
                        .limit(size)
                        .offset(from)
                        .fetch();
            }
            case FUTURE: {
                return queryFactory.selectFrom(booking)
                        .where(bookerOrOwner)
                        .where(booking.startDate.after(LocalDateTime.now()))
                        .orderBy(booking.startDate.desc())
                        .limit(size)
                        .offset(from)
                        .fetch();
            }
        }
        return null;

    }

    @Override
    public Booking findBookingById(long bookingId) {
        return repository.findById(bookingId).get();
    }

    @Override
    public boolean existsId(long bookingId) {
        return repository.existsById(bookingId);
    }

    @Override
    public void resetDb() {
        repository.clearDb();
    }
}
