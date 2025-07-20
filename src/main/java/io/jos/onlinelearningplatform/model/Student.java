package io.jos.onlinelearningplatform.model;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@NoArgsConstructor

public class Student extends User {

    @ManyToMany(mappedBy = "enrolledStudents")
    private List<Lesson> enrolledLessons = new ArrayList<>();
    @ManyToMany(mappedBy = "students")
    private List<Payment> payments = new ArrayList<>();


    public Student(String fullName, String username, String email, String passwordHash) {
        super(fullName, username, email, passwordHash);
        setUserRole(Role.STUDENT);

    }

}
