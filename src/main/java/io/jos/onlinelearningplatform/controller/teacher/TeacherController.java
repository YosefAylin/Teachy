package io.jos.onlinelearningplatform.controller.teacher;

import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final LessonRepository lessonRepository;


    public TeacherController(TeacherService teacherService, LessonRepository lessonRepository) {
        this.teacherService = teacherService;
        this.lessonRepository = lessonRepository;
    }

    private Long getCurrentTeacherId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return teacherService.getTeacherIdByUsername(auth.getName());
    }

    @GetMapping("/home")
    public String teacherHome(Model model) {
        Long tid = getCurrentTeacherId();
        model.addAttribute("nextLesson", teacherService.getNextLesson(tid));
        model.addAttribute("lessonCount", teacherService.getLessonCount(tid));
        return "teacher/home";
    }


    @GetMapping("/students")
    public String viewStudents(Model model) {
        model.addAttribute("students", teacherService.getStudents(getCurrentTeacherId()));
        return "teacher/students";
    }


    @GetMapping("/content")
    public String createContent() {
        return "teacher/content";
    }

    @GetMapping("/lessons")
    public String viewAllLessons(Model model) {
        model.addAttribute("lessons",
                Optional.ofNullable(teacherService.getAllLessonsOrdered(getCurrentTeacherId()))
                        .orElse(Collections.emptyList()));
        return "teacher/lessons";
    }

    @ModelAttribute("newLessonRequestCount")
    public int getNewLessonRequestCount() {
        return teacherService.countPendingLessons(getCurrentTeacherId());
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Long teacherId = getCurrentTeacherId();

        var teacher  = teacherService.getTeacherProfile(teacherId);
        var current  = teacherService.getTeachableCourses(teacherId);
        var all      = teacherService.getAvailableCourses();

        var available = all.stream()
                .filter(c -> current.stream().noneMatch(cc -> cc.getId().equals(c.getId())))
                .toList();

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", current);            // teachable courses
        model.addAttribute("availableCourses", available); // for the dropdown
        return "teacher/profile";
    }

    @PostMapping("/courses/add")
    public String addTeachableCourse(@RequestParam Long courseId) {
        teacherService.addTeachableCourse(getCurrentTeacherId(), courseId);
        return "redirect:/teacher/profile?updated";
    }

    @PostMapping("/courses/{courseId}/remove")
    public String removeTeachableCourse(@PathVariable Long courseId) {
        teacherService.removeTeachableCourse(getCurrentTeacherId(), courseId);
        return "redirect:/teacher/profile?updated";
    }

    @PostMapping("/lessons/{id}/accept")
    public String accept(@PathVariable Long id) {
        teacherService.acceptLesson(id);
        return "redirect:/home?accepted";
    }

    @PostMapping("/lessons/{id}/reject")
    public String reject(@PathVariable Long id) {
        io.jos.onlinelearningplatform.model.Lesson l =
                lessonRepository.findById(id).orElseThrow();
        l.setStatus("REJECTED");
        lessonRepository.save(l);
        return "redirect:/home?rejected";
    }


}