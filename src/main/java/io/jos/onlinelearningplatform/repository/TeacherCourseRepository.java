package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {

    @Query("SELECT tc FROM TeacherCourse tc WHERE tc.teacher.id = :teacherId")
    List<TeacherCourse> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT CASE WHEN COUNT(tc) > 0 THEN true ELSE false END FROM TeacherCourse tc WHERE tc.teacher.id = :teacherId AND tc.course.id = :courseId")
    boolean existsByTeacherIdAndCourseId(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeacherCourse tc WHERE tc.teacher.id = :teacherId AND tc.course.id = :courseId")
    void deleteByTeacherIdAndCourseId(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);

    @Query("SELECT DISTINCT tc.teacher FROM TeacherCourse tc WHERE tc.course.id = :courseId")
    List<Teacher> findTeachersByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT tc FROM TeacherCourse tc WHERE tc.course.id = :courseId")
    List<TeacherCourse> findByCourseId(@Param("courseId") Long courseId);
}
