package io.jos.onlinelearningplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "admins")
public class Admin extends User {

    public Admin(String fullName, String username, String email, String passwordHash) {
        super(fullName, username, email, passwordHash);
        setUserRole(Role.ADMIN);
    }
}
