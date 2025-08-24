package io.jos.onlinelearningplatform.controller.teacher;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.MessageRepository;
import io.jos.onlinelearningplatform.repository.StudyMaterialRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final LessonRepository lessonRepository;
    private final MessageRepository messageRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final UserRepository userRepository;

    public TeacherController(TeacherService teacherService, LessonRepository lessonRepository, MessageRepository messageRepository, StudyMaterialRepository studyMaterialRepository, UserRepository userRepository) {
        this.teacherService = teacherService;
        this.lessonRepository = lessonRepository;
        this.messageRepository = messageRepository;
        this.studyMaterialRepository = studyMaterialRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentTeacherId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return teacherService.getTeacherIdByUsername(auth.getName());
    }

    @GetMapping("/home")
    public String teacherHome(Model model) {
        Long tid = getCurrentTeacherId();
        model.addAttribute("nextLesson", teacherService.getNextLesson(tid));
        model.addAttribute("lessonCount", teacherService.getLessonCount(tid));
        return "teacher/home";
    }


    @GetMapping("/students")
    public String viewStudents(Model model) {
        model.addAttribute("students", teacherService.getStudents(getCurrentTeacherId()));
        return "teacher/students";
    }


    @GetMapping("/content")
    public String createContent() {
        return "teacher/content";
    }

    @GetMapping("/lessons")
    public String viewAllLessons(Model model) {
        Long teacherId = getCurrentTeacherId();
        List<Lesson> upcoming = teacherService.getUpcomingLessonsForTeacher(teacherId);
        List<Lesson> past = teacherService.getPastLessonsForTeacher(teacherId);

        model.addAttribute("upcoming", upcoming);
        model.addAttribute("past", past);
        return "teacher/lessons";
    }

    @ModelAttribute("newLessonRequestCount")
    public int getNewLessonRequestCount() {
        return teacherService.countPendingLessons(getCurrentTeacherId());
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Long teacherId = getCurrentTeacherId();

        var teacher  = teacherService.getTeacherProfile(teacherId);
        var current  = teacherService.getTeachableCourses(teacherId);
        var all      = teacherService.getAvailableCourses();

        var available = all.stream()
                .filter(c -> current.stream().noneMatch(cc -> cc.getId().equals(c.getId())))
                .toList();

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", current);            // teachable courses
        model.addAttribute("availableCourses", available); // for the dropdown
        return "teacher/profile";
    }

    @PostMapping("/courses/add")
    public String addTeachableCourse(@RequestParam Long courseId) {
        teacherService.addTeachableCourse(getCurrentTeacherId(), courseId);
        return "redirect:/teacher/profile?updated";
    }

    @PostMapping("/courses/{courseId}/remove")
    public String removeTeachableCourse(@PathVariable Long courseId) {
        teacherService.removeTeachableCourse(getCurrentTeacherId(), courseId);
        return "redirect:/teacher/profile?updated";
    }

    @PostMapping("/lessons/{id}/accept")
    public String accept(@PathVariable Long id) {
        teacherService.acceptLesson(id);
        return "redirect:/home?accepted";
    }

    @PostMapping("/lessons/{id}/reject")
    public String reject(@PathVariable Long id) {
        io.jos.onlinelearningplatform.model.Lesson l =
                lessonRepository.findById(id).orElseThrow();
        l.setStatus("REJECTED");
        lessonRepository.save(l);
        return "redirect:/home?rejected";
    }

    @GetMapping("/schedule")
    public String viewSchedule(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               Model model) {
        Long teacherId = getCurrentTeacherId();

        // Default to current month if not specified
        LocalDateTime now = LocalDateTime.now();
        int currentYear = year != null ? year : now.getYear();
        int currentMonth = month != null ? month : now.getMonthValue();

        // Get first and last day of the month
        LocalDateTime startOfMonth = LocalDateTime.of(currentYear, currentMonth, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusDays(1).withHour(23).withMinute(59);

        // Get schedules for the month
        List<Schedule> monthlySchedules = teacherService.getSchedulesForMonth(teacherId, startOfMonth, endOfMonth);

        // Create calendar data
        model.addAttribute("schedules", monthlySchedules);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("monthName", startOfMonth.getMonth().name());
        model.addAttribute("daysInMonth", startOfMonth.toLocalDate().lengthOfMonth());
        model.addAttribute("firstDayOfWeek", startOfMonth.getDayOfWeek().getValue() % 7);

        return "teacher/schedule";
    }

    @GetMapping("/lessons/{id}")
    public String viewLesson(@PathVariable Long id, Model model) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        // Verify this teacher owns the lesson
        if (!lesson.getTeacher().getId().equals(getCurrentTeacherId())) {
            throw new IllegalArgumentException("Access denied");
        }

        model.addAttribute("lesson", lesson);
        model.addAttribute("messages", messageRepository.findByLessonIdOrderBySentAtAsc(id));
        model.addAttribute("materials", studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(id));
        model.addAttribute("currentUserId", getCurrentTeacherId());
        model.addAttribute("userType", "teacher");

        return "teacher/lesson-detail";
    }

    @PostMapping("/lessons/{id}/message")
    public String sendMessage(@PathVariable Long id, @RequestParam String content) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getTeacher().getId().equals(getCurrentTeacherId())) {
            throw new IllegalArgumentException("Access denied");
        }

        Message message = new Message();
        message.setLesson(lesson);
        message.setSender(teacherService.getTeacherProfile(getCurrentTeacherId()));
        message.setContent(content);
        messageRepository.save(message);

        return "redirect:/teacher/lessons/" + id;
    }

    @PostMapping("/lessons/{id}/upload")
    public String uploadMaterial(@PathVariable Long id,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "description", required = false) String description) {

        if (file.isEmpty()) {
            return "redirect:/teacher/lessons/" + id + "?error=empty";
        }

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getTeacher().getId().equals(getCurrentTeacherId())) {
            throw new IllegalArgumentException("Access denied");
        }

        try {
            StudyMaterial material = new StudyMaterial();
            material.setLesson(lesson);
            material.setUploader(teacherService.getTeacherProfile(getCurrentTeacherId()));
            material.setFileName(file.getOriginalFilename());
            material.setFileSize(file.getSize());
            material.setDescription(description);
            material.setFileData(file.getBytes());

            studyMaterialRepository.save(material);

        } catch (IOException e) {
            return "redirect:/teacher/lessons/" + id + "?error=upload";
        }

        return "redirect:/teacher/lessons/" + id + "?uploaded=1";
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

    // Student Profile for Teacher View
    @GetMapping("/students/{id}/profile")
    public String viewStudentProfile(@PathVariable Long id, Model model) {
        Long currentTeacherId = getCurrentTeacherId();

        // Get the student
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User is not a student");
        }

        Student student = (Student) user;

        // Get all lessons for this student
        List<Lesson> allStudentLessons = lessonRepository.findByStudentIdOrderByTimestampDesc(student.getId());

        // Filter lessons to only show those involving the current teacher
        List<Lesson> lessonsWithThisTeacher = allStudentLessons.stream()
                .filter(lesson -> lesson.getTeacher().getId().equals(currentTeacherId))
                .collect(Collectors.toList());

        // Get recent lessons with this teacher (last 10)
        List<Lesson> recentLessons = lessonsWithThisTeacher.stream()
                .limit(10)
                .collect(Collectors.toList());

        // Calculate statistics for lessons with this teacher
        long totalLessonsWithTeacher = lessonsWithThisTeacher.size();
        long completedLessons = lessonsWithThisTeacher.stream()
                .filter(lesson -> "ACCEPTED".equals(lesson.getStatus()))
                .count();
        long pendingLessons = lessonsWithThisTeacher.stream()
                .filter(lesson -> "PENDING".equals(lesson.getStatus()))
                .count();

        // Get unique courses this student has taken with this teacher
        Set<Course> coursesSet = new HashSet<>();
        for (Lesson lesson : lessonsWithThisTeacher) {
            coursesSet.add(lesson.getCourse());
        }
        List<Course> courses = new ArrayList<>(coursesSet);

        // Verify teacher has taught this student
        if (totalLessonsWithTeacher == 0) {
            throw new IllegalArgumentException("You have not taught this student");
        }

        model.addAttribute("student", student);
        model.addAttribute("allLessons", lessonsWithThisTeacher);
        model.addAttribute("recentLessons", recentLessons);
        model.addAttribute("totalLessons", totalLessonsWithTeacher);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("pendingLessons", pendingLessons);
        model.addAttribute("courses", courses);
        model.addAttribute("currentTeacher", teacherService.getTeacherProfile(currentTeacherId));

        return "teacher/student-profile";
    }
}