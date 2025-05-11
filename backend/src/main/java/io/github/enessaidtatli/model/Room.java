package io.github.enessaidtatli.model;

import io.github.enessaidtatli.config.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity(name = "room")
@Table(name = "t_room")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Room extends Audit {

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "user_room",
            joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<User> users;

    @OneToMany(mappedBy = "room")
    private List<ChatMessage> messages;

}