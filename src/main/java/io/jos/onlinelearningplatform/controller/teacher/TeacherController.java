package io.jos.onlinelearningplatform.controller.teacher;

import io.jos.onlinelearningplatform.facade.TeacherFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherFacade teacherFacade;

    public TeacherController(TeacherFacade teacherFacade) {
        this.teacherFacade = teacherFacade;
    }

    @GetMapping("/home")
    public String teacherHome(Model model) {
        return teacherFacade.prepareHomePage(model);
    }

    @GetMapping("/students")
    public String viewStudents(Model model) {
        return teacherFacade.prepareStudentsPage(model);
    }

    @GetMapping("/content")
    public String createContent() {
        return teacherFacade.getContentPage();
    }

    @GetMapping("/lessons")
    public String viewAllLessons(Model model) {
        return teacherFacade.prepareLessonsPage(model);
    }

    @ModelAttribute("newLessonRequestCount")
    public int getNewLessonRequestCount() {
        return teacherFacade.getNewLessonRequestCount();
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        return teacherFacade.prepareProfilePage(model);
    }

    @PostMapping("/courses/add")
    public String addTeachableCourse(@RequestParam Long courseId) {
        return teacherFacade.addTeachableCourse(courseId);
    }

    @PostMapping("/courses/{courseId}/remove")
    public String removeTeachableCourse(@PathVariable Long courseId) {
        return teacherFacade.removeTeachableCourse(courseId);
    }

    @PostMapping("/lessons/{id}/accept")
    public String accept(@PathVariable Long id) {
        return teacherFacade.acceptLesson(id);
    }

    @PostMapping("/lessons/{id}/reject")
    public String reject(@PathVariable Long id) {
        return teacherFacade.rejectLesson(id);
    }

    @GetMapping("/schedule")
    public String viewSchedule(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               Model model) {
        return teacherFacade.prepareSchedulePage(year, month, model);
    }

    @GetMapping("/lessons/{id}")
    public String viewLesson(@PathVariable Long id, Model model) {
        return teacherFacade.prepareLessonDetail(id, model);
    }

    @PostMapping("/lessons/{id}/message")
    public String sendMessage(@PathVariable Long id, @RequestParam String content) {
        return teacherFacade.sendMessage(id, content);
    }

    @PostMapping("/lessons/{id}/upload")
    public String uploadMaterial(@PathVariable Long id,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "description", required = false) String description) {
        return teacherFacade.uploadMaterial(id, file, description);
    }

    @GetMapping("/materials/{materialId}/download")
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable Long materialId) {
        return teacherFacade.downloadMaterial(materialId);
    }

    @GetMapping("/students/{id}/profile")
    public String viewStudentProfile(@PathVariable Long id, Model model) {
        return teacherFacade.prepareStudentProfile(id, model);
    }
}