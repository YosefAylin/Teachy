package io.jos.onlinelearningplatform.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Lesson {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String CourseName;
    private String title;
    private String contentUrl; // URL to the lesson content (video, document, etc.)
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime timestamp = LocalDateTime.now();




}
