package io.github.enessaidtatli.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "role")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Role  {

    @Id
    @Column
    private Long id;

    @Column(nullable = false)
    private String name;

}
