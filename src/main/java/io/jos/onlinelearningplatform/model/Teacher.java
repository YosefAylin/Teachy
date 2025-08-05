package io.jos.onlinelearningplatform.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TEACHER")
@NoArgsConstructor
@Getter @Setter
public class Teacher extends User {

    @ManyToMany
    @JoinTable(
            name = "teacher_courses",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Lesson> meetings = new ArrayList<>();

    @OneToMany(mappedBy = "teacher")
    private List<Payment> receivedPayments = new ArrayList<>();

    public Teacher(String username, String email, String hash) {
        super(username, email, hash, "TEACHER");
    }
}
