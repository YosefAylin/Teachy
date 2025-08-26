package io.jos.onlinelearningplatform.facade;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.MessageRepository;
import io.jos.onlinelearningplatform.repository.StudyMaterialRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.TeacherService;
import io.jos.onlinelearningplatform.util.UserUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TeacherFacade {

    private final TeacherService teacherService;
    private final LessonRepository lessonRepository;
    private final MessageRepository messageRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final UserRepository userRepository;
    private final UserUtils userUtils;

    public TeacherFacade(TeacherService teacherService, LessonRepository lessonRepository,
                        MessageRepository messageRepository, StudyMaterialRepository studyMaterialRepository,
                        UserRepository userRepository, UserUtils userUtils) {
        this.teacherService = teacherService;
        this.lessonRepository = lessonRepository;
        this.messageRepository = messageRepository;
        this.studyMaterialRepository = studyMaterialRepository;
        this.userRepository = userRepository;
        this.userUtils = userUtils;
    }

    public String prepareHomePage(Model model) {
        Long teacherId = userUtils.getCurrentTeacherId();
        model.addAttribute("nextLesson", teacherService.getNextLesson(teacherId));
        model.addAttribute("lessonCount", teacherService.getLessonCount(teacherId));
        return "teacher/home";
    }

    public String prepareStudentsPage(Model model) {
        model.addAttribute("students", teacherService.getStudents(userUtils.getCurrentTeacherId()));
        return "teacher/students";
    }

    public String getContentPage() {
        return "teacher/content";
    }

    public String prepareLessonsPage(Model model) {
        Long teacherId = userUtils.getCurrentTeacherId();
        List<Lesson> upcoming = teacherService.getUpcomingLessonsForTeacher(teacherId);
        List<Lesson> past = teacherService.getPastLessonsForTeacher(teacherId);

        model.addAttribute("upcoming", upcoming);
        model.addAttribute("past", past);
        return "teacher/lessons";
    }

    public int getNewLessonRequestCount() {
        return teacherService.countPendingLessons(userUtils.getCurrentTeacherId());
    }

    public String prepareProfilePage(Model model) {
        Long teacherId = userUtils.getCurrentTeacherId();

        Teacher teacher = teacherService.getTeacherProfile(teacherId);
        List<Course> current = teacherService.getTeachableCourses(teacherId);
        List<Course> all = teacherService.getAvailableCourses();

        List<Course> available = all.stream()
                .filter(c -> current.stream().noneMatch(cc -> cc.getId().equals(c.getId())))
                .toList();

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", current);
        model.addAttribute("availableCourses", available);
        return "teacher/profile";
    }

    public String addTeachableCourse(Long courseId) {
        teacherService.addTeachableCourse(userUtils.getCurrentTeacherId(), courseId);
        return "redirect:/teacher/profile?updated";
    }

    public String removeTeachableCourse(Long courseId) {
        teacherService.removeTeachableCourse(userUtils.getCurrentTeacherId(), courseId);
        return "redirect:/teacher/profile?updated";
    }

    public String acceptLesson(Long lessonId) {
        teacherService.acceptLesson(lessonId);
        return "redirect:/home?accepted";
    }

    public String rejectLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        lesson.setStatus("REJECTED");
        lessonRepository.save(lesson);
        return "redirect:/home?rejected";
    }

    public String prepareSchedulePage(Integer year, Integer month, Model model) {
        Long teacherId = userUtils.getCurrentTeacherId();

        LocalDateTime now = LocalDateTime.now();
        int currentYear = year != null ? year : now.getYear();
        int currentMonth = month != null ? month : now.getMonthValue();

        LocalDateTime startOfMonth = LocalDateTime.of(currentYear, currentMonth, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusDays(1).withHour(23).withMinute(59);

        List<Schedule> monthlySchedules = teacherService.getSchedulesForMonth(teacherId, startOfMonth, endOfMonth);

        model.addAttribute("schedules", monthlySchedules);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("monthName", startOfMonth.getMonth().name());
        model.addAttribute("daysInMonth", startOfMonth.toLocalDate().lengthOfMonth());
        model.addAttribute("firstDayOfWeek", startOfMonth.getDayOfWeek().getValue() % 7);

        return "teacher/schedule";
    }

    public String prepareLessonDetail(Long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        validateTeacherAccess(lesson);

        model.addAttribute("lesson", lesson);
        model.addAttribute("messages", messageRepository.findByLessonIdOrderBySentAtAsc(lessonId));
        model.addAttribute("materials", studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(lessonId));
        model.addAttribute("currentUserId", userUtils.getCurrentTeacherId());
        model.addAttribute("userType", "teacher");

        return "teacher/lesson-detail";
    }

    public String sendMessage(Long lessonId, String content) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        validateTeacherAccess(lesson);

        Message message = new Message();
        message.setLesson(lesson);
        message.setSender(teacherService.getTeacherProfile(userUtils.getCurrentTeacherId()));
        message.setContent(content);
        messageRepository.save(message);

        return "redirect:/teacher/lessons/" + lessonId;
    }

    public String uploadMaterial(Long lessonId, MultipartFile file, String description) {
        if (file.isEmpty()) {
            return "redirect:/teacher/lessons/" + lessonId + "?error=empty";
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        validateTeacherAccess(lesson);

        try {
            StudyMaterial material = new StudyMaterial();
            material.setLesson(lesson);
            material.setUploader(teacherService.getTeacherProfile(userUtils.getCurrentTeacherId()));
            material.setFileName(file.getOriginalFilename());
            material.setFileSize(file.getSize());
            material.setDescription(description);
            material.setFileData(file.getBytes());

            studyMaterialRepository.save(material);
        } catch (IOException e) {
            return "redirect:/teacher/lessons/" + lessonId + "?error=upload";
        }

        return "redirect:/teacher/lessons/" + lessonId + "?uploaded=1";
    }

    public ResponseEntity<byte[]> downloadMaterial(Long materialId) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + material.getFileName() + "\"")
                .body(material.getFileData());
    }

    public String prepareStudentProfile(Long studentId, Model model) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User is not a student");
        }

        Student student = (Student) user;

        // Get lessons this teacher has with this student
        Long currentTeacherId = userUtils.getCurrentTeacherId();
        List<Lesson> sharedLessons = teacherService.getLessonsWithStudent(currentTeacherId, studentId);

        // Calculate statistics
        int totalLessons = sharedLessons.size();
        long completedLessons = sharedLessons.stream()
                .filter(lesson -> "COMPLETED".equals(lesson.getStatus()))
                .count();
        long pendingLessons = sharedLessons.stream()
                .filter(lesson -> "PENDING".equals(lesson.getStatus()))
                .count();

        // Get unique courses they've worked on together
        List<Course> courses = sharedLessons.stream()
                .map(Lesson::getCourse)
                .distinct()
                .toList();

        // Get recent lessons (last 10)
        List<Lesson> recentLessons = sharedLessons.stream()
                .limit(10)
                .toList();

        model.addAttribute("student", student);
        model.addAttribute("sharedLessons", sharedLessons);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("pendingLessons", pendingLessons);
        model.addAttribute("courses", courses);
        model.addAttribute("recentLessons", recentLessons);

        return "teacher/student-profile";
    }

    private void validateTeacherAccess(Lesson lesson) {
        if (!lesson.getTeacher().getId().equals(userUtils.getCurrentTeacherId())) {
            throw new IllegalArgumentException("Access denied");
        }
    }
}
