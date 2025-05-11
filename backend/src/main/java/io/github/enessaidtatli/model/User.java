package io.github.enessaidtatli.model;

import io.github.enessaidtatli.config.audit.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.Set;

@Entity(name = "user")
@Table(name = "t_user", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class User extends Audit {

    @Column(nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, updatable = false)
    private String phoneNumber;

    @ManyToMany
    @JoinTable(
            name = "t_user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;
}
