package io.jos.onlinelearningplatform.facade;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.LessonService;
import io.jos.onlinelearningplatform.service.StudentService;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class StudentFacade {

    private final CourseRepository courseRepository;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final UserRepository userRepository;
    private final StudentService studentService;
    private final LessonRepository lessonRepository;
    private final MessageRepository messageRepository;
    private final StudyMaterialRepository studyMaterialRepository;

    public StudentFacade(CourseRepository courseRepository, TeacherService teacherService,
                        LessonService lessonService, UserRepository userRepository,
                        StudentService studentService, LessonRepository lessonRepository,
                        MessageRepository messageRepository, StudyMaterialRepository studyMaterialRepository) {
        this.courseRepository = courseRepository;
        this.teacherService = teacherService;
        this.lessonService = lessonService;
        this.userRepository = userRepository;
        this.studentService = studentService;
        this.lessonRepository = lessonRepository;
        this.messageRepository = messageRepository;
        this.studyMaterialRepository = studyMaterialRepository;
    }

    public String prepareHomePage(Model model) {
        Long studentId = getCurrentStudentId();
        model.addAttribute("courseCount", courseRepository.count());
        model.addAttribute("nextLesson", studentService.getNextLesson(studentId));
        return "student/home";
    }

    public String prepareSearchPage(Long courseId, Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);

        if (courseId != null) {
            List<Teacher> teachers = teacherService.findTeachersByCourse(courseId);
            model.addAttribute("teachers", teachers);
        }
        return "student/search";
    }

    public String prepareLessonsPage(Model model) {
        Long studentId = getCurrentStudentId();
        List<Lesson> upcoming = lessonService.getUpcomingForStudent(studentId);
        List<Lesson> past = lessonService.getPastForStudent(studentId);
        model.addAttribute("upcoming", upcoming);
        model.addAttribute("past", past);
        return "student/my-lessons";
    }

    public String prepareTeacherProfile(Long teacherId, Model model) {
        Teacher teacher = teacherService.getTeacherProfile(teacherId);
        List<Course> courses = teacherService.getTeachableCourses(teacherId);
        model.addAttribute("teacher", teacher);
        model.addAttribute("teacherCourses", courses);
        return "student/teacher-profile";
    }

    public String prepareLessonRequestForm(Long teacherId, Long courseId, Model model) {
        Teacher teacher = teacherService.getTeacherProfile(teacherId);
        List<Course> teacherCourses = teacherService.getTeachableCourses(teacherId);
        model.addAttribute("teacher", teacher);
        model.addAttribute("teacherCourses", teacherCourses);
        model.addAttribute("selectedCourseId", courseId);
        return "student/request-lesson";
    }

    public String submitLessonRequest(Long teacherId, Long courseId, int month, int day, int hour) {
        LocalDateTime date = LocalDateTime.of(2025, month, day, hour, 0);
        Long studentId = getCurrentStudentId();
        lessonService.requestLesson(studentId, teacherId, courseId, date);
        return "redirect:/student/lessons?requested=1";
    }

    public String prepareSchedulePage(Integer year, Integer month, Model model) {
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

    public String prepareLessonDetail(Long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        validateStudentAccess(lesson);

        model.addAttribute("lesson", lesson);
        model.addAttribute("messages", messageRepository.findByLessonIdOrderBySentAtAsc(lessonId));
        model.addAttribute("materials", studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(lessonId));
        model.addAttribute("currentUserId", getCurrentStudentId());
        model.addAttribute("userType", "student");

        return "student/lesson-detail";
    }

    public String sendMessage(Long lessonId, String content) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        validateStudentAccess(lesson);

        Message message = new Message();
        message.setLesson(lesson);
        message.setSender(userRepository.findById(getCurrentStudentId()).orElseThrow());
        message.setContent(content);
        messageRepository.save(message);

        return "redirect:/student/lessons/" + lessonId;
    }

    public String uploadMaterial(Long lessonId, MultipartFile file, String description) {
        if (file.isEmpty()) {
            return "redirect:/student/lessons/" + lessonId + "?error=empty";
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        validateStudentAccess(lesson);

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
            return "redirect:/student/lessons/" + lessonId + "?error=upload";
        }

        return "redirect:/student/lessons/" + lessonId + "?uploaded=1";
    }

    public ResponseEntity<byte[]> downloadMaterial(Long materialId) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + material.getFileName() + "\"")
                .body(material.getFileData());
    }

    private Long getCurrentStudentId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + username));

        if (!(user instanceof Student)) {
            throw new IllegalStateException("Logged-in user is not a STUDENT: " + username);
        }

        return user.getId();
    }

    private void validateStudentAccess(Lesson lesson) {
        if (!lesson.getStudent().getId().equals(getCurrentStudentId())) {
            throw new IllegalArgumentException("Access denied");
        }
    }
}
