package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import java.util.Set;

@Builder
@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 130, unique = true)
    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Item> items;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private Set<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requestor")
    private Set<ItemRequest> requests;
}
