package io.jos.onlinelearningplatform.facade;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;

@Component
public class AdminFacade {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final MessageRepository messageRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final AdminService adminService;

    public AdminFacade(UserRepository userRepository, CourseRepository courseRepository,
                      LessonRepository lessonRepository, MessageRepository messageRepository,
                      TeacherCourseRepository teacherCourseRepository, AdminService adminService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.messageRepository = messageRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.adminService = adminService;
    }

    public String prepareHomePage(Model model) {
        model.addAttribute("totalStudents", adminService.getTotalStudents());
        model.addAttribute("totalTeachers", adminService.getTotalTeachers());
        model.addAttribute("totalCourses", adminService.getTotalCourses());
        model.addAttribute("totalLessons", adminService.getTotalLessons());
        return "admin/home";
    }

    public String prepareUsersPage(int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("username"));
        Page<User> users = userRepository.findAll(pageable);

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        return "admin/users";
    }

    public String toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    public String prepareCoursesPage(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("newCourse", new Course());
        return "admin/courses";
    }

    public String createCourse(Course course) {
        courseRepository.save(course);
        return "redirect:/admin/courses?created=1";
    }

    @Transactional
    public String deleteCourse(Long courseId) {
        List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByTimestampDesc(courseId);

        for (Lesson lesson : courseLessons) {
            List<Message> lessonMessages = messageRepository.findByLessonIdOrderBySentAtAsc(lesson.getId());
            if (!lessonMessages.isEmpty()) {
                messageRepository.deleteAll(lessonMessages);
            }
            lessonRepository.deleteById(lesson.getId());
        }

        List<TeacherCourse> teacherCourses = teacherCourseRepository.findByCourseId(courseId);
        if (!teacherCourses.isEmpty()) {
            teacherCourseRepository.deleteAll(teacherCourses);
        }

        courseRepository.deleteById(courseId);
        return "redirect:/admin/courses?deleted=1";
    }

    public String prepareLessonsPage(int page, Model model) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by("timestamp").descending());
        Page<Lesson> lessons = lessonRepository.findAll(pageable);

        model.addAttribute("lessons", lessons);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", lessons.getTotalPages());
        return "admin/lessons";
    }

    public String prepareReportsPage(Model model) {
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByUserType(Student.class);
        long totalTeachers = userRepository.countByUserType(Teacher.class);
        long totalAdmins = userRepository.countByUserType(Admin.class);

        long totalLessons = lessonRepository.count();
        long pendingLessons = lessonRepository.countByStatus("PENDING");
        long acceptedLessons = lessonRepository.countByStatus("ACCEPTED");
        long rejectedLessons = lessonRepository.countByStatus("REJECTED");

        long totalCourses = courseRepository.count();

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

    public String prepareCourseDetailsPage(Long courseId, Model model) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Get course lessons
        List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByTimestampDesc(courseId);

        // Get unique students and teachers from lessons
        List<Student> students = courseLessons.stream()
                .map(Lesson::getStudent)
                .distinct()
                .toList();

        List<Teacher> teachers = courseLessons.stream()
                .map(Lesson::getTeacher)
                .distinct()
                .toList();

        // Calculate statistics
        long totalLessons = courseLessons.size();
        long completedLessons = courseLessons.stream()
                .filter(lesson -> "COMPLETED".equals(lesson.getStatus()))
                .count();

        // Get recent lessons (last 10)
        List<Lesson> recentLessons = courseLessons.stream()
                .limit(10)
                .toList();

        model.addAttribute("course", course);
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalTeachers", teachers.size());
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("recentLessons", recentLessons);

        return "admin/course-details";
    }

    public String prepareLessonDetailsPage(Long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        model.addAttribute("lesson", lesson);
        return "admin/lesson-details";
    }

    public String approveLessonRequest(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lesson.setStatus("ACCEPTED");
        lessonRepository.save(lesson);

        return "redirect:/admin/lessons/" + lessonId + "/details?approved=1";
    }

    public String rejectLessonRequest(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lesson.setStatus("REJECTED");
        lessonRepository.save(lesson);

        return "redirect:/admin/lessons/" + lessonId + "/details?rejected=1";
    }

    public String cancelLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lesson.setStatus("CANCELLED");
        lessonRepository.save(lesson);

        return "redirect:/admin/lessons/" + lessonId + "/details?cancelled=1";
    }
}
