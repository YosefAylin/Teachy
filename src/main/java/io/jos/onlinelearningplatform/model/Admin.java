package io.jos.onlinelearningplatform.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin(String username, String email, String hash) {
    }
}
