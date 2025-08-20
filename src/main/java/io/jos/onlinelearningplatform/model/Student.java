package io.jos.onlinelearningplatform.model;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("STUDENT")
@NoArgsConstructor
public class Student extends User {

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Lesson> attendedLessons = new ArrayList<>();


    public Student(String username, String email, String hash) {
        super(username, email, hash, "STUDENT");
    }
}
