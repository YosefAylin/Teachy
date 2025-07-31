package io.jos.onlinelearningplatform.model;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TEACHER")
@NoArgsConstructor

public class Teacher extends User {

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Lesson> taughtLessons = new ArrayList<>();
    @OneToMany(mappedBy = "teacher")
    private List<Payment> receivedPayments = new ArrayList<>();

    public Teacher(String username, String email, String hash) {
        super(username, email, hash, "TEACHER");
    }
}
