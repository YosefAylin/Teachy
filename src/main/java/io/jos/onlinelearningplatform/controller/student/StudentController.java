// src/main/java/io/jos/onlinelearningplatform/controller/student/StudentController.java
package io.jos.onlinelearningplatform.controller.student;
import org.springframework.web.multipart.MultipartFile;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.LessonService;
import io.jos.onlinelearningplatform.service.StudentService;
import io.jos.onlinelearningplatform.service.TeacherService;
import io.jos.onlinelearningplatform.service.impl.StudentServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final CourseRepository courseRepository;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final UserRepository userRepository;
    private final StudentService studentService;
    private final LessonRepository lessonRepository;
    private final MessageRepository messageRepository;
    private final StudyMaterialRepository studyMaterialRepository;


    public StudentController(CourseRepository courseRepository, TeacherService teacherService, LessonService lessonService, UserRepository userRepository, StudentService studentService, LessonRepository lessonRepository, MessageRepository messageRepository, StudyMaterialRepository studyMaterialRepository) {
        this.courseRepository = courseRepository;
        this.teacherService = teacherService;
        this.lessonService = lessonService;
        this.userRepository = userRepository;
        this.studentService = studentService;
        this.lessonRepository = lessonRepository;
        this.messageRepository = messageRepository;
        this.studyMaterialRepository = studyMaterialRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {
        Long studentId = getCurrentStudentId();
        model.addAttribute("courseCount", courseRepository.count());
        model.addAttribute("nextLesson", studentService.getNextLesson(studentId));
        return "student/home";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required=false) Long courseId , Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);

        if (courseId != null) {
            java.util.List<io.jos.onlinelearningplatform.model.Teacher> teachers =
                    teacherService.findTeachersByCourse(courseId);
            model.addAttribute("teachers", teachers);
        }
        return "student/search";
    }

    @GetMapping("/lessons")
    public String myLessons(Model model) {
        Long studentId = getCurrentStudentId();
        List<Lesson> upcoming = lessonService.getUpcomingForStudent(studentId);
        List<Lesson> past = lessonService.getPastForStudent(studentId);
        model.addAttribute("upcoming", upcoming);
        model.addAttribute("past", past);
        return "student/my-lessons";
    }



    @GetMapping("/teacher/profile/{id}")
    public String viewTeacher(@PathVariable Long id, org.springframework.ui.Model model) {
        io.jos.onlinelearningplatform.model.Teacher teacher = teacherService.getTeacherProfile(id);
        model.addAttribute("teacher", teacher);
        java.util.List<io.jos.onlinelearningplatform.model.Course> courses =
                teacherService.getTeachableCourses(id);
        model.addAttribute("teacherCourses", courses);
        return "student/teacher-profile";
    }

    @GetMapping("/lessons/request")
    public String requestLessonForm(@RequestParam Long teacherId,@RequestParam Long courseId, Model model) {
        Teacher teacher = teacherService.getTeacherProfile(teacherId);
        List<Course> teacherCourses = teacherService.getTeachableCourses(teacherId);
        model.addAttribute("teacher", teacher);
        model.addAttribute("teacherCourses", teacherCourses);
        model.addAttribute("selectedCourseId", courseId);
        return "student/request-lesson";
    }

    @PostMapping("/lessons/request")
    public String submitLessonRequest(@RequestParam Long teacherId, @RequestParam Long courseId,
                                      @RequestParam int month, @RequestParam int day, @RequestParam int hour) {

        LocalDateTime date = LocalDateTime.of(2025, month, day, hour, 0);
        Long studentId = getCurrentStudentId();
        lessonService.requestLesson(studentId, teacherId, courseId, date);
        return "redirect:/student/lessons?requested=1";
    }




    private Long getCurrentStudentId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + username));

        // Optional: ensure it’s actually a Student
        if (!(user instanceof Student)) {
            throw new IllegalStateException("Logged-in user is not a STUDENT: " + username);
        }

        return user.getId(); // id is on User base class
    }

    @GetMapping("/schedule")
    public String viewSchedule(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               Model model) {
        Long studentId = getCurrentStudentId();

        LocalDateTime now = LocalDateTime.now();
        int currentYear = year != null ? year : now.getYear();
        int currentMonth = month != null ? month : now.getMonthValue();

        LocalDateTime startOfMonth = LocalDateTime.of(currentYear, currentMonth, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusDays(1).withHour(23).withMinute(59);

        List<Schedule> monthlySchedules = studentService.getSchedulesForMonth(studentId, startOfMonth, endOfMonth);

        model.addAttribute("schedules", monthlySchedules);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("monthName", startOfMonth.getMonth().name());
        model.addAttribute("daysInMonth", startOfMonth.toLocalDate().lengthOfMonth());
        model.addAttribute("firstDayOfWeek", startOfMonth.getDayOfWeek().getValue() % 7);

        return "student/schedule";
    }


    @GetMapping("/lessons/{id}")
    public String viewLesson(@PathVariable Long id, Model model) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        // Verify this student owns the lesson
        if (!lesson.getStudent().getId().equals(getCurrentStudentId())) {
            throw new IllegalArgumentException("Access denied");
        }

        model.addAttribute("lesson", lesson);
        model.addAttribute("messages", messageRepository.findByLessonIdOrderBySentAtAsc(id));
        model.addAttribute("materials", studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(id));
        model.addAttribute("currentUserId", getCurrentStudentId());
        model.addAttribute("userType", "student");

        return "student/lesson-detail";
    }

    @PostMapping("/lessons/{id}/message")
    public String sendMessage(@PathVariable Long id, @RequestParam String content) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getStudent().getId().equals(getCurrentStudentId())) {
            throw new IllegalArgumentException("Access denied");
        }

        Message message = new Message();
        message.setLesson(lesson);
        message.setSender(userRepository.findById(getCurrentStudentId()).orElseThrow());
        message.setContent(content);
        messageRepository.save(message);

        return "redirect:/student/lessons/" + id;
    }

    @PostMapping("/lessons/{id}/upload")
    public String uploadMaterial(@PathVariable Long id,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "description", required = false) String description) {

        if (file.isEmpty()) {
            return "redirect:/student/lessons/" + id + "?error=empty";
        }

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getStudent().getId().equals(getCurrentStudentId())) {
            throw new IllegalArgumentException("Access denied");
        }

        try {
            StudyMaterial material = new StudyMaterial();
            material.setLesson(lesson);
            material.setUploader(userRepository.findById(getCurrentStudentId()).orElseThrow());
            material.setFileName(file.getOriginalFilename());
            material.setFileSize(file.getSize());
            material.setDescription(description);
            material.setFileData(file.getBytes());

            studyMaterialRepository.save(material);

        } catch (IOException e) {
            return "redirect:/student/lessons/" + id + "?error=upload";
        }

        return "redirect:/student/lessons/" + id + "?uploaded=1";
    }

    @GetMapping("/materials/{materialId}/download")
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable Long materialId) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        // Verify access permissions here

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + material.getFileName() + "\"")
                .body(material.getFileData());
    }
}
