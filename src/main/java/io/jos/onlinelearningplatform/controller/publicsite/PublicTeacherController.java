package io.jos.onlinelearningplatform.controller.publicsite;

import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
public class PublicTeacherController {

    private final TeacherService teacherService;

    public PublicTeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Teacher t = teacherService.getTeacherProfile(id);
        if (t == null) return "redirect:/student/search?notfound";
        var courses = teacherService.getTeachableCourses(id);

        model.addAttribute("teacher", t);
        model.addAttribute("courses", courses);
        return "public/teacher-profile";
    }
}
