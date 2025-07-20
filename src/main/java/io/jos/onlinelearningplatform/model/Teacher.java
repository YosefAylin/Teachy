package io.jos.onlinelearningplatform.model;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@NoArgsConstructor

public class Teacher extends User {

    private List<Lesson> taughtLessons = new ArrayList<>();
    @OneToMany(mappedBy = "teacher")
    private List<Payment> receivedPayments = new ArrayList<>();

    public Teacher(String fullName, String username, String email, String passwordHash) {
        super(fullName, username, email, passwordHash);
        setUserRole(Role.TEACHER);
    }
}
