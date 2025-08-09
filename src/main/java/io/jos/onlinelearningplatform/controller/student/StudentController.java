// src/main/java/io/jos/onlinelearningplatform/controller/student/StudentController.java
package io.jos.onlinelearningplatform.controller.student;

import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.LessonService;
import io.jos.onlinelearningplatform.service.StudentService;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final CourseRepository courseRepository;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final UserRepository userRepository;

    public StudentController(CourseRepository courseRepository, TeacherService teacherService, LessonService lessonService, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.teacherService = teacherService;
        this.lessonService = lessonService;
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("courseCount", courseRepository.count());
        return "student/home";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) Long courseId, org.springframework.ui.Model model) {
        java.util.List<io.jos.onlinelearningplatform.model.Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);

        if (courseId != null) {
            java.util.List<io.jos.onlinelearningplatform.model.Teacher> teachers =
                    teacherService.findTeachersByCourse(courseId);
            model.addAttribute("teachers", teachers);
        }
        return "student/search";
    }

    @GetMapping("/lessons")
    public String myLessons(org.springframework.ui.Model model) {
        Long studentId = getCurrentStudentId();
        java.util.List<io.jos.onlinelearningplatform.model.Lesson> upcoming =
                lessonService.getUpcomingForStudent(studentId);
        java.util.List<io.jos.onlinelearningplatform.model.Lesson> past =
                lessonService.getPastForStudent(studentId);
        model.addAttribute("upcoming", upcoming);
        model.addAttribute("past", past);
        return "student/my-lessons";
    }



    @GetMapping("/teacher/{id}")
    public String viewTeacher(@PathVariable Long id, org.springframework.ui.Model model) {
        io.jos.onlinelearningplatform.model.Teacher teacher = teacherService.getTeacherProfile(id);
        model.addAttribute("teacher", teacher);
        java.util.List<io.jos.onlinelearningplatform.model.Course> courses =
                teacherService.getTeachableCourses(id);
        model.addAttribute("teacherCourses", courses);
        return "student/teacher-profile";
    }

    @GetMapping("/lessons/request")
    public String requestLessonForm(@RequestParam Long teacherId,
                                    @RequestParam(required=false) Long courseId,
                                    org.springframework.ui.Model model) {
        io.jos.onlinelearningplatform.model.Teacher teacher = teacherService.getTeacherProfile(teacherId);
        java.util.List<io.jos.onlinelearningplatform.model.Course> teacherCourses =
                teacherService.getTeachableCourses(teacherId);
        model.addAttribute("teacher", teacher);
        model.addAttribute("teacherCourses", teacherCourses);
        model.addAttribute("selectedCourseId", courseId);
        return "student/request-lesson";
    }

    @PostMapping("/lessons/request")
    public String submitLessonRequest(@RequestParam Long teacherId,
                                      @RequestParam Long courseId,
                                      @RequestParam("scheduledAt")
                                      @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
                                      java.time.LocalDateTime scheduledAt,
                                      @RequestParam(required = false) String note,
                                      org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Long studentId = getCurrentStudentId();
        try {
            lessonService.requestLesson(studentId, teacherId, courseId, scheduledAt, note);
            ra.addFlashAttribute("toast", "Lesson request sent.");
            return "redirect:/student/lessons";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/student/lessons/request?teacherId=" + teacherId +
                    (courseId != null ? "&courseId=" + courseId : "");
        }
    }




    private Long getCurrentStudentId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + username));

        // Optional: ensure it’s actually a Student
        if (!(user instanceof Student)) {
            throw new IllegalStateException("Logged-in user is not a STUDENT: " + username);
        }

        return user.getId(); // id is on User base class
    }
}
