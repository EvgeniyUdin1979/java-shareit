package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Builder
@Entity
@Table(name = "Items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private Set<Comment> comments;

    @Column(name = "request_Id")
    private Long requestId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
