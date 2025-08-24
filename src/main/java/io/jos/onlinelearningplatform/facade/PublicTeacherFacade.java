package io.jos.onlinelearningplatform.facade;

import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class PublicTeacherFacade {

    private final TeacherService teacherService;

    public PublicTeacherFacade(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public String prepareTeacherProfile(Long teacherId, Model model) {
        Teacher teacher = teacherService.getTeacherProfile(teacherId);
        if (teacher == null) {
            return "redirect:/student/search?notfound";
        }

        var courses = teacherService.getTeachableCourses(teacherId);
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);
        return "public/teacher-profile";
    }
}
