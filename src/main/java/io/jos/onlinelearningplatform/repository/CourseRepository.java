package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

}