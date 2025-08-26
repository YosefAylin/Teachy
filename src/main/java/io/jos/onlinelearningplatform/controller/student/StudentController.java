// src/main/java/io/jos/onlinelearningplatform/controller/student/StudentController.java
package io.jos.onlinelearningplatform.controller.student;

import io.jos.onlinelearningplatform.facade.StudentFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentFacade studentFacade;

    public StudentController(StudentFacade studentFacade) {
        this.studentFacade = studentFacade;
    }

    @GetMapping("/home")
    public String home(Model model) {
        return studentFacade.prepareHomePage(model);
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) Long courseId, Model model) {
        return studentFacade.prepareSearchPage(courseId, model);
    }

    @GetMapping("/lessons")
    public String myLessons(Model model) {
        return studentFacade.prepareLessonsPage(model);
    }

    @GetMapping("/teacher/profile/{id}")
    public String viewTeacher(@PathVariable Long id, Model model) {
        return studentFacade.prepareTeacherProfile(id, model);
    }

    @GetMapping("/lessons/request")
    public String requestLessonForm(@RequestParam Long teacherId, @RequestParam Long courseId, Model model) {
        return studentFacade.prepareLessonRequestForm(teacherId, courseId, model);
    }

    @PostMapping("/lessons/request")
    public String submitLessonRequest(@RequestParam Long teacherId, @RequestParam Long courseId,
                                      @RequestParam int month, @RequestParam int day, @RequestParam int hour) {
        return studentFacade.submitLessonRequest(teacherId, courseId, month, day, hour);
    }

    @GetMapping("/schedule")
    public String viewSchedule(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               Model model) {
        return studentFacade.prepareSchedulePage(year, month, model);
    }

    @GetMapping("/lessons/{id}")
    public String viewLesson(@PathVariable Long id, Model model) {
        return studentFacade.prepareLessonDetail(id, model);
    }

    @PostMapping("/lessons/{id}/message")
    public String sendMessage(@PathVariable Long id, @RequestParam String content) {
        return studentFacade.sendMessage(id, content);
    }

    @PostMapping("/lessons/{id}/upload")
    public String uploadMaterial(@PathVariable Long id,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "description", required = false) String description) {
        return studentFacade.uploadMaterial(id, file, description);
    }

    @GetMapping("/materials/{materialId}/download")
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable Long materialId) {
        return studentFacade.downloadMaterial(materialId);
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        return studentFacade.prepareProfilePage(model);
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String email,
                               @RequestParam(required = false) String currentPassword,
                               @RequestParam(required = false) String newPassword,
                               @RequestParam(required = false) String confirmPassword) {
        return studentFacade.updateProfile(email, currentPassword, newPassword, confirmPassword);
    }
}
