package io.github.enessaidtatli.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    private String id;
    private String name;
    private String creator;
    private Set<String> users;

    public Room(String name, String creator) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.creator = creator;
        this.users = new HashSet<>();
        this.users.add(creator);
    }
}