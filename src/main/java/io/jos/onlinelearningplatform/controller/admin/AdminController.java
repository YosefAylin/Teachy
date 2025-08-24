package io.jos.onlinelearningplatform.controller.admin;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final MessageRepository messageRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final AdminService adminService;

    public AdminController(UserRepository userRepository, CourseRepository courseRepository, LessonRepository lessonRepository, MessageRepository messageRepository, TeacherCourseRepository teacherCourseRepository, AdminService adminService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.messageRepository = messageRepository;
        this.teacherCourseRepository = teacherCourseRepository;
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
        // Step 1: Get all lessons for this course
        List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByTimestampDesc(id);

        // Step 2 & 3: Delete each lesson individually with proper message cleanup
        for (Lesson lesson : courseLessons) {
            // Delete all messages related to this lesson first
            List<Message> lessonMessages = messageRepository.findByLessonIdOrderBySentAtAsc(lesson.getId());
            if (!lessonMessages.isEmpty()) {
                messageRepository.deleteAll(lessonMessages);
            }
            // Then delete the lesson itself
            lessonRepository.deleteById(lesson.getId());
        }

        // Step 4: Delete all teacher-course relationships for this course
        List<TeacherCourse> teacherCourses = teacherCourseRepository.findByCourseId(id);
        if (!teacherCourses.isEmpty()) {
            teacherCourseRepository.deleteAll(teacherCourses);
        }

        // Step 5: Now safely delete the course
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

    // Student Profile for Admin View
    @GetMapping("/users/{id}/student-profile")
    public String viewStudentProfile(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User is not a student");
        }

        Student student = (Student) user;

        // Get student's lessons using the correct method
        List<Lesson> allLessons = lessonRepository.findByStudentIdOrderByTimestampDesc(student.getId());
        List<Lesson> recentLessons = allLessons.stream().limit(10).collect(Collectors.toList());

        // Calculate statistics
        long totalLessons = allLessons.size();
        long completedLessons = allLessons.stream()
                .filter(lesson -> "ACCEPTED".equals(lesson.getStatus()))
                .count();
        long pendingLessons = allLessons.stream()
                .filter(lesson -> "PENDING".equals(lesson.getStatus()))
                .count();

        // Get unique teachers and courses
        Set<Teacher> teachersSet = new HashSet<>();
        Set<Course> coursesSet = new HashSet<>();

        for (Lesson lesson : allLessons) {
            teachersSet.add(lesson.getTeacher());
            coursesSet.add(lesson.getCourse());
        }

        List<Teacher> teachers = new ArrayList<>(teachersSet);
        List<Course> courses = new ArrayList<>(coursesSet);

        model.addAttribute("student", student);
        model.addAttribute("allLessons", allLessons);
        model.addAttribute("recentLessons", recentLessons);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("pendingLessons", pendingLessons);
        model.addAttribute("uniqueTeachers", teachersSet.size());
        model.addAttribute("teachers", teachers);
        model.addAttribute("courses", courses);

        return "admin/student-profile";
    }

    // Teacher Profile for Admin View
    @GetMapping("/users/{id}/teacher-profile")
    public String viewTeacherProfile(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Teacher)) {
            throw new IllegalArgumentException("User is not a teacher");
        }

        Teacher teacher = (Teacher) user;

        // Get teacher's lessons using the correct method
        List<Lesson> allLessons = lessonRepository.findByTeacherId(teacher.getId());
        List<Lesson> recentLessons = allLessons.stream()
                .sorted((l1, l2) -> l2.getTimestamp().compareTo(l1.getTimestamp()))
                .limit(10)
                .collect(Collectors.toList());

        // Calculate statistics
        long totalLessons = allLessons.size();
        long completedLessons = allLessons.stream()
                .filter(lesson -> "ACCEPTED".equals(lesson.getStatus()))
                .count();
        long pendingLessons = allLessons.stream()
                .filter(lesson -> "PENDING".equals(lesson.getStatus()))
                .count();

        // Get unique students this teacher has taught
        Set<Student> studentsSet = new HashSet<>();
        for (Lesson lesson : allLessons) {
            studentsSet.add(lesson.getStudent());
        }
        List<Student> students = new ArrayList<>(studentsSet);

        // Get teachable courses for this teacher
        List<TeacherCourse> teacherCourses = teacherCourseRepository.findByTeacherId(teacher.getId());
        List<Course> teachableCourses = teacherCourses.stream()
                .map(TeacherCourse::getCourse)
                .collect(Collectors.toList());

        model.addAttribute("teacher", teacher);
        model.addAttribute("allLessons", allLessons);
        model.addAttribute("recentLessons", recentLessons);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("pendingLessons", pendingLessons);
        model.addAttribute("uniqueStudents", studentsSet.size());
        model.addAttribute("students", students);
        model.addAttribute("teachableCourses", teachableCourses);

        return "admin/teacher-profile";
    }

    // Course Details for Admin View
    @GetMapping("/courses/{id}/details")
    public String viewCourseDetails(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Get all lessons for this course using the correct method
        List<Lesson> allLessons = lessonRepository.findByCourseIdOrderByTimestampDesc(course.getId());
        List<Lesson> recentLessons = allLessons.stream().limit(20).collect(Collectors.toList());

        // Get unique students and teachers for this course
        Set<Student> studentsSet = new HashSet<>();
        Set<Teacher> teachersSet = new HashSet<>();

        for (Lesson lesson : allLessons) {
            studentsSet.add(lesson.getStudent());
            teachersSet.add(lesson.getTeacher());
        }

        List<Student> students = new ArrayList<>(studentsSet);
        List<Teacher> teachers = new ArrayList<>(teachersSet);

        // Calculate statistics
        long totalLessons = allLessons.size();
        long completedLessons = allLessons.stream()
                .filter(lesson -> "ACCEPTED".equals(lesson.getStatus()))
                .count();

        model.addAttribute("course", course);
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        model.addAttribute("recentLessons", recentLessons);
        model.addAttribute("totalStudents", studentsSet.size());
        model.addAttribute("totalTeachers", teachersSet.size());
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedLessons", completedLessons);

        return "admin/course-details";
    }

    // Lesson Details for Admin View
    @GetMapping("/lessons/{id}/details")
    public String viewLessonDetails(@PathVariable Long id, Model model) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        model.addAttribute("lesson", lesson);
        return "admin/lesson-details";
    }

    // Lesson Management Actions
    @PostMapping("/lessons/{id}/approve")
    public String approveLesson(@PathVariable Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lesson.setStatus("ACCEPTED");
        lessonRepository.save(lesson);

        return "redirect:/admin/lessons/" + id + "/details";
    }

    @PostMapping("/lessons/{id}/reject")
    public String rejectLesson(@PathVariable Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lesson.setStatus("REJECTED");
        lessonRepository.save(lesson);

        return "redirect:/admin/lessons/" + id + "/details";
    }

    @PostMapping("/lessons/{id}/cancel")
    public String cancelLesson(@PathVariable Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lesson.setStatus("CANCELLED");
        lessonRepository.save(lesson);

        return "redirect:/admin/lessons/" + id + "/details";
    }

    @PostMapping("/lessons/{id}/delete")
    public String deleteLesson(@PathVariable Long id) {
        // First, delete all messages related to this lesson to avoid foreign key constraint violation
        List<Message> relatedMessages = messageRepository.findByLessonIdOrderBySentAtAsc(id);
        if (!relatedMessages.isEmpty()) {
            messageRepository.deleteAll(relatedMessages);
        }

        // Now safely delete the lesson
        lessonRepository.deleteById(id);
        return "redirect:/admin/lessons?deleted=1";
    }
}
