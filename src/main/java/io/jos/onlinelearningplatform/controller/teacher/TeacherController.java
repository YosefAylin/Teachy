package io.jos.onlinelearningplatform.controller.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @GetMapping("/home")
    public String teacherHome() {
        return "teacher/home";
    }
}
