package jos.onlinelearningplatform.service;

public interface LessonService {
    void addLesson(Long courseId, String lessonTitle, String lessonContent);
    void removeLesson(Long lessonId);
    void updateLesson(Long lessonId, String newTitle, String newContent);
    void viewLessonsForCourse(Long courseId);
    void viewLessonDetails(Long lessonId);
}
