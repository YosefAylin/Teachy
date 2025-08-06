package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.model.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    List<TeacherCourse> findByTeacherId(Long teacherId);
    boolean existsByTeacherIdAndCourseId(Long teacherId, Long courseId);
    void deleteByTeacherIdAndCourseId(Long teacherId, Long courseId);
}
