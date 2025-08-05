package io.jos.onlinelearningplatform.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private double amount;
    private String description;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Payment() {
        // Default constructor for JPA
    }
        public Payment(double amount, String description, Student s, Teacher t) {
        this.amount = amount;
        this.description = description;
        this.student = s;
        this.teacher = t;
        this.timestamp = LocalDateTime.now();
    }


}
