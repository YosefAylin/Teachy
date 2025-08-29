package io.jos.onlinelearningplatform.controller.admin;

import io.jos.onlinelearningplatform.facade.AdminFacade;
import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminFacade adminFacade;
    private final UserRepository userRepository;

    public AdminController(AdminFacade adminFacade, UserRepository userRepository) {
        this.adminFacade = adminFacade;
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String adminHome(Model model) {
        return adminFacade.prepareHomePage(model);
    }

    @GetMapping("/users")
    public String manageUsers(@RequestParam(defaultValue = "0") int page, Model model) {
        return adminFacade.prepareUsersPage(page, model);
    }

    @PostMapping("/users/{id}/toggle-active")
    public String toggleUserActive(@PathVariable Long id) {
        return adminFacade.toggleUserActive(id);
    }

    @GetMapping("/courses")
    public String manageCourses(Model model) {
        return adminFacade.prepareCoursesPage(model);
    }

    @PostMapping("/courses")
    public String createCourse(@ModelAttribute Course course) {
        return adminFacade.createCourse(course);
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        return adminFacade.deleteCourse(id);
    }

    @GetMapping("/lessons")
    public String manageLessons(@RequestParam(defaultValue = "0") int page, Model model) {
        return adminFacade.prepareLessonsPage(page, model);
    }

    @GetMapping("/reports")
    public String systemReports(Model model) {
        return adminFacade.prepareReportsPage(model);
    }

    @GetMapping("/courses/{id}/details")
    public String courseDetails(@PathVariable Long id, Model model) {
        return adminFacade.prepareCourseDetailsPage(id, model);
    }

    @GetMapping("/lessons/{id}/details")
    public String lessonDetails(@PathVariable Long id, Model model) {
        return adminFacade.prepareLessonDetailsPage(id, model);
    }

    @PostMapping("/lessons/{id}/approve")
    public String approveLesson(@PathVariable Long id) {
        return adminFacade.approveLessonRequest(id);
    }

    @PostMapping("/lessons/{id}/reject")
    public String rejectLesson(@PathVariable Long id) {
        return adminFacade.rejectLessonRequest(id);
    }

    @PostMapping("/lessons/{id}/cancel")
    public String cancelLesson(@PathVariable Long id) {
        return adminFacade.cancelLesson(id);
    }

    @GetMapping("/users/{id}/teacher-profile")
    public String viewTeacherProfile(@PathVariable Long id, Model model) {
        return adminFacade.prepareTeacherProfilePage(id, model);
    }

    @GetMapping("/users/{id}/student-profile")
    public String viewStudentProfile(@PathVariable Long id, Model model) {
        return adminFacade.prepareStudentProfilePage(id, model);
    }

    @GetMapping("/users/{id}/admin-profile")
    public String viewAdminProfile(@PathVariable Long id, Model model) {
        return adminFacade.prepareAdminProfilePage(id, model);
    }

    @GetMapping("/profile")
    public String viewCurrentAdminProfile(Model model, Principal principal) {
        // Get current admin user ID from the authenticated principal
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        return adminFacade.prepareAdminProfilePage(currentUser.getId(), model);
    }
}
