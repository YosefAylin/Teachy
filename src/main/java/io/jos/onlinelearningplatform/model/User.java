package io.jos.onlinelearningplatform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;


    @Column(nullable = false)
    private boolean connected = false;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true; // Initialize with default value



    public User(String username, String email, String hash, String role) {
        this.username = username;
        this.email = email;
        this.passwordHash = hash;
        this.connected = false;
    }
}

