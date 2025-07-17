package jos.onlinelearningplatform.service.impl;

public interface LessonServiceImpl {
    void addLesson(Long courseId, String lessonTitle, String lessonContent);
    void removeLesson(Long lessonId);
    void updateLesson(Long lessonId, String newTitle, String newContent);
    void viewLessonsForCourse(Long courseId);
    void viewLessonDetails(Long lessonId);
}
