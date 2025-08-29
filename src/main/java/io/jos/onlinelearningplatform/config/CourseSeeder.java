package io.jos.onlinelearningplatform.config;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class CourseSeeder implements ApplicationRunner {
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final LessonRepository lessonRepo;
    private final MessageRepository messageRepo;
    private final TeacherCourseRepository teacherCourseRepo;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    public CourseSeeder(CourseRepository courseRepo, UserRepository userRepo,
                       LessonRepository lessonRepo, MessageRepository messageRepo,
                       TeacherCourseRepository teacherCourseRepo, PasswordEncoder passwordEncoder) {
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.lessonRepo = lessonRepo;
        this.messageRepo = messageRepo;
        this.teacherCourseRepo = teacherCourseRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Only seed if database is empty
        if (userRepo.count() > 2) return;

        // Create courses
        List<Course> courses = createCourses();

        // Create users (students and teachers)
        List<Student> students = createStudents();
        List<Teacher> teachers = createTeachers();

        // Create lessons
        List<Lesson> lessons = createLessons(courses, students, teachers);

        // Create teacher-course relationships based on lessons
        createTeacherCourseRelationships(lessons);

        // Create messages
        createMessages(lessons);

        System.out.println("Demo data seeded successfully!");
        System.out.println("Created: " + courses.size() + " courses, " +
                          students.size() + " students, " + teachers.size() + " teachers, " +
                          lessons.size() + " lessons");
    }

    private List<Course> createCourses() {
        List<Course> courses = List.of(
                make("Hebrew Language (Reading & Writing)", "HEBREW",
                        "Reading fluency, writing basics, comprehension."),
                make("Mathematics", "MATHEMATICS",
                        "Numbers, operations, shapes, measurement."),
                make("English", "ENGLISH",
                        "Basic vocabulary, reading and simple conversation."),
                make("Science & Technology","SCIENCE_TECHNOLOGY",
                        "Plants/animals, weather, materials, simple experiments."),
                make("Homeland, Society & Civics", "HOMELAND_SOCIETY_CIVICS",
                        "Community, rules, responsibility, cooperation."),
                make("History & Geography", "HISTORY_GEOGRAPHY",
                        "Maps, continents, early history stories."),
                make("Jewish Culture / Tanakh", "JEWISH_CULTURE_TANAKH",
                        "Selected stories and values (as applicable)."),
                make("Arts & Music", "ARTS_MUSIC",
                        "Drawing, crafting, rhythm, simple instruments."),
                make("Physical Education", "PHYSICAL_EDUCATION",
                        "Movement, coordination, games, teamwork."),
                make("Computers / ICT", "COMPUTERS_ICT",
                        "Basic typing, safe use, simple apps.")
        );
        return courseRepo.saveAll(courses);
    }

    private List<Student> createStudents() {
        String hashedPassword = passwordEncoder.encode("password123");
        List<Student> students = List.of(
                new Student("alice_cooper", "alice.cooper@email.com", hashedPassword),
                new Student("bob_smith", "bob.smith@email.com", hashedPassword),
                new Student("charlie_brown", "charlie.brown@email.com", hashedPassword),
                new Student("diana_prince", "diana.prince@email.com", hashedPassword),
                new Student("eva_green", "eva.green@email.com", hashedPassword),
                new Student("frank_miller", "frank.miller@email.com", hashedPassword),
                new Student("grace_kelly", "grace.kelly@email.com", hashedPassword),
                new Student("henry_ford", "henry.ford@email.com", hashedPassword)
        );
        return userRepo.saveAll(students);
    }

    private List<Teacher> createTeachers() {
        String hashedPassword = passwordEncoder.encode("teacher123");
        List<Teacher> teachers = List.of(
                new Teacher("prof_johnson", "prof.johnson@email.com", hashedPassword),
                new Teacher("ms_williams", "ms.williams@email.com", hashedPassword),
                new Teacher("dr_davis", "dr.davis@email.com", hashedPassword),
                new Teacher("mr_wilson", "mr.wilson@email.com", hashedPassword),
                new Teacher("prof_anderson", "prof.anderson@email.com", hashedPassword)
        );
        return userRepo.saveAll(teachers);
    }

    private List<Lesson> createLessons(List<Course> courses, List<Student> students, List<Teacher> teachers) {
        List<Lesson> lessons = List.of(
                // Upcoming lessons
                createLesson(courses.get(0), students.get(0), teachers.get(0),
                           "Introduction to Hebrew Reading", "ACCEPTED", LocalDateTime.now().plusDays(1)),
                createLesson(courses.get(1), students.get(1), teachers.get(1),
                           "Basic Arithmetic Operations", "ACCEPTED", LocalDateTime.now().plusDays(2)),
                createLesson(courses.get(2), students.get(2), teachers.get(2),
                           "English Conversation Practice", "ACCEPTED", LocalDateTime.now().plusDays(3)),
                createLesson(courses.get(3), students.get(3), teachers.get(3),
                           "Science Experiments", "PENDING", LocalDateTime.now().plusDays(4)),
                createLesson(courses.get(4), students.get(4), teachers.get(4),
                           "Civics and Community", "ACCEPTED", LocalDateTime.now().plusDays(5)),

                // Past lessons
                createLesson(courses.get(0), students.get(5), teachers.get(0),
                           "Hebrew Writing Practice", "COMPLETED", LocalDateTime.now().minusDays(2)),
                createLesson(courses.get(1), students.get(6), teachers.get(1),
                           "Geometry Basics", "COMPLETED", LocalDateTime.now().minusDays(3)),
                createLesson(courses.get(2), students.get(7), teachers.get(2),
                           "English Grammar Review", "COMPLETED", LocalDateTime.now().minusDays(1)),

                // More varied lessons
                createLesson(courses.get(5), students.get(0), teachers.get(3),
                           "World Geography", "ACCEPTED", LocalDateTime.now().plusDays(6)),
                createLesson(courses.get(7), students.get(1), teachers.get(4),
                           "Music Theory Basics", "PENDING", LocalDateTime.now().plusDays(7)),
                createLesson(courses.get(9), students.get(2), teachers.get(0),
                           "Computer Basics", "ACCEPTED", LocalDateTime.now().plusDays(8)),

                // Some rejected lessons for demo purposes
                createLesson(courses.get(6), students.get(3), teachers.get(1),
                           "Cultural Studies", "REJECTED", LocalDateTime.now().plusDays(9))
        );
        return lessonRepo.saveAll(lessons);
    }

    private void createMessages(List<Lesson> lessons) {
        // Create messages for completed and accepted lessons
        for (Lesson lesson : lessons) {
            if ("COMPLETED".equals(lesson.getStatus()) || "ACCEPTED".equals(lesson.getStatus())) {
                // Teacher message
                Message teacherMsg = new Message();
                teacherMsg.setLesson(lesson);
                teacherMsg.setSender(lesson.getTeacher());
                teacherMsg.setContent(getRandomTeacherMessage());
                teacherMsg.setSentAt(lesson.getTimestamp().minusHours(1));

                // Student reply
                Message studentMsg = new Message();
                studentMsg.setLesson(lesson);
                studentMsg.setSender(lesson.getStudent());
                studentMsg.setContent(getRandomStudentMessage());
                studentMsg.setSentAt(lesson.getTimestamp().minusMinutes(30));

                // Another teacher message
                Message teacherFollowUp = new Message();
                teacherFollowUp.setLesson(lesson);
                teacherFollowUp.setSender(lesson.getTeacher());
                teacherFollowUp.setContent(getRandomTeacherFollowUp());
                teacherFollowUp.setSentAt(lesson.getTimestamp().minusMinutes(15));

                messageRepo.saveAll(List.of(teacherMsg, studentMsg, teacherFollowUp));
            }
        }
    }

    private Lesson createLesson(Course course, Student student, Teacher teacher,
                               String description, String status, LocalDateTime timestamp) {
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setStudent(student);
        lesson.setTeacher(teacher);
        lesson.setDescription(description);
        lesson.setStatus(status);
        lesson.setTimestamp(timestamp);
        return lesson;
    }

    private void createTeacherCourseRelationships(List<Lesson> lessons) {
        for (Lesson lesson : lessons) {
            TeacherCourse teacherCourse = new TeacherCourse();
            teacherCourse.setTeacher(lesson.getTeacher());
            teacherCourse.setCourse(lesson.getCourse());
            teacherCourseRepo.save(teacherCourse);
        }
    }

    private String getRandomTeacherMessage() {
        String[] messages = {
            "Hello! I'm looking forward to our lesson today. Please make sure you have your materials ready.",
            "Great job on the homework! Let's continue with the next topic in today's session.",
            "Don't forget to review the material we covered last time before our meeting.",
            "I've prepared some interactive exercises for today's lesson. You'll find them engaging!",
            "Please let me know if you have any questions before we start our lesson.",
            "Today we'll be focusing on practical applications of what you've learned so far."
        };
        return messages[random.nextInt(messages.length)];
    }

    private String getRandomStudentMessage() {
        String[] messages = {
            "Thank you! I've completed the assigned reading and I'm ready for today's lesson.",
            "I have a few questions about the homework. Can we discuss them during our session?",
            "I'm excited about today's topic! I've been looking forward to learning more about this.",
            "I practiced the exercises you gave me. I think I'm getting better at this!",
            "Thanks for the feedback on my last assignment. I'll keep working on improving.",
            "I'm ready for today's lesson. See you soon!"
        };
        return messages[random.nextInt(messages.length)];
    }

    private String getRandomTeacherFollowUp() {
        String[] messages = {
            "Perfect! I can see you're making great progress. Keep up the excellent work!",
            "Absolutely! We'll address all your questions during our session today.",
            "That's wonderful to hear! Your enthusiasm makes teaching so much more rewarding.",
            "Excellent! Practice makes perfect, and your dedication is really showing.",
            "You're very welcome! Remember, improvement takes time, and you're doing great.",
            "Looking forward to our session! See you at the scheduled time."
        };
        return messages[random.nextInt(messages.length)];
    }

    private Course make(String title, String subject, String desc) {
        Course c = new Course();
        c.setTitle(title);
        c.setSubject(subject);
        c.setDescription(desc);
        return c;
    }
}
