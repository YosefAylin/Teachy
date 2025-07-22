package io.jos.onlinelearningplatform.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity
public class Lesson {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String CourseName;
    private String title;
    private String contentUrl; // URL to the lesson content (video, document, etc.)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime timestamp = LocalDateTime.now();
    @ManyToMany
    @JoinTable(name = "enrolled_students",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> enrolledStudents;




}
