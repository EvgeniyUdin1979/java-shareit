package ru.practicum.shareit.item.srorages.dao;

import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository {
   Comment add(long userId, long itemId, String text);
}
