package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    List<TeacherCourse> findByTeacherId(Long teacherId);
    boolean existsByTeacherIdAndCourseId(Long teacherId, Long courseId);
    void deleteByTeacherIdAndCourseId(Long teacherId, Long courseId);
    @Query("""
        select distinct tc.teacher
          from TeacherCourse tc
         where tc.course.id = :courseId
    """)
    List<Teacher> findTeachersByCourseId(@Param("courseId") Long courseId);
    List<TeacherCourse> findByCourseId(Long courseId);
    void deleteByCourseId(Long courseId);
}
