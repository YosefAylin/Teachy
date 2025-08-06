package io.jos.onlinelearningplatform.controller.teacher;

import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;


    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    private Long getCurrentTeacherId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return teacherService.getTeacherIdByUsername(auth.getName());
    }

    @GetMapping("/home")
    public String teacherHome() {
        return "teacher/home";
    }

    @GetMapping("/courses")
    public String viewCourses(Model model) {
        model.addAttribute("courses", teacherService.getTeachableCourses(getCurrentTeacherId()));
        return "teacher/courses";
    }

    @GetMapping("/students")
    public String viewStudents(Model model) {
        model.addAttribute("students", teacherService.getStudents(getCurrentTeacherId()));
        return "teacher/students";
    }

    @GetMapping("/schedule")
    public String viewSchedule(Model model) {
        model.addAttribute("sessions", teacherService.getUpcomingLessons(getCurrentTeacherId()));
        return "teacher/schedule";
    }


    @GetMapping("/content")
    public String createContent() {
        return "teacher/content";
    }

    @GetMapping("/lessons/active")
    public String viewActiveLessons(Model model) {
        model.addAttribute("lessons", teacherService.getAcceptedLessons(getCurrentTeacherId()));
        return "teacher/lessons_accepted";
    }

    @GetMapping("/lessons/pending")
    public String viewPendingLessons(Model model) {
        model.addAttribute("lessons", teacherService.getPendingLessons(getCurrentTeacherId()));
        return "teacher/lessons_pending";
    }

    @ModelAttribute("newLessonRequestCount")
    public int getNewLessonRequestCount() {
        return teacherService.countPendingLessons(getCurrentTeacherId());
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Long teacherId = getCurrentTeacherId();
        model.addAttribute("teacher", teacherService.getTeacherProfile(teacherId));
        model.addAttribute("courses", teacherService.getTeachableCourses(teacherId));
        return "teacher/profile";
    }
}