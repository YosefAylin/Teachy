package io.jos.onlinelearningplatform.controller.admin;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.AdminService;
import io.jos.onlinelearningplatform.service.impl.AdminServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final AdminService adminService;

    public AdminController(UserRepository userRepository, CourseRepository courseRepository, LessonRepository lessonRepository, AdminService adminService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.adminService = adminService;
    }

    @GetMapping("/home")
    public String adminHome(Model model) {
        // Add statistics to the model
        model.addAttribute("totalStudents", adminService.getTotalStudents());
        model.addAttribute("totalTeachers", adminService.getTotalTeachers());
        model.addAttribute("totalCourses", adminService.getTotalCourses());
        model.addAttribute("totalLessons", adminService.getTotalLessons());

        return "admin/home";
    }

    @GetMapping("/users")
    public String manageUsers(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("username"));
        Page<User> users = userRepository.findAll(pageable);

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-active")
    public String toggleUserActive(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/courses")
    public String manageCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("newCourse", new Course());
        return "admin/courses";
    }

    @PostMapping("/courses")
    public String createCourse(@ModelAttribute Course course) {
        courseRepository.save(course);
        return "redirect:/admin/courses?created=1";
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/courses?deleted=1";
    }

    @GetMapping("/lessons")
    public String manageLessons(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by("timestamp").descending());
        Page<Lesson> lessons = lessonRepository.findAll(pageable);

        model.addAttribute("lessons", lessons);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", lessons.getTotalPages());
        return "admin/lessons";
    }

    @GetMapping("/reports")
    public String systemReports(Model model) {
        // User statistics
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByUserType(Student.class);
        long totalTeachers = userRepository.countByUserType(Teacher.class);
        long totalAdmins = userRepository.countByUserType(Admin.class);

        // Lesson statistics
        long totalLessons = lessonRepository.count();
        long pendingLessons = lessonRepository.countByStatus("PENDING");
        long acceptedLessons = lessonRepository.countByStatus("ACCEPTED");
        long rejectedLessons = lessonRepository.countByStatus("REJECTED");

        // Course statistics
        long totalCourses = courseRepository.count();

        // Monthly trends (last 12 months)

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("pendingLessons", pendingLessons);
        model.addAttribute("acceptedLessons", acceptedLessons);
        model.addAttribute("rejectedLessons", rejectedLessons);
        model.addAttribute("totalCourses", totalCourses);

        return "admin/reports";
    }

}