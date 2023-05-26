package ru.practicum.shareit.user.storages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

public interface UserStorageJpaRepository extends JpaRepository<User, Long> {
    @Transactional
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users;ALTER TABLE users ALTER COLUMN id RESTART WITH 1;",nativeQuery = true)
    void clearDb();
}
