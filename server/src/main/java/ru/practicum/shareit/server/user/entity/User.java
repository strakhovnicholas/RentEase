package ru.practicum.shareit.server.user.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.user.enums.UserRole;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {
    @ToString.Include
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen")
    @SequenceGenerator(name = "mySeqGen", sequenceName = "id_sequence", allocationSize = 1)
    private Long id;

    @ToString.Include
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    private String password;

    @ToString.Include
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}

