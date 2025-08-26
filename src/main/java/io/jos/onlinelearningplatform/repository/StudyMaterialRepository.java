package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {

    @Query("SELECT sm FROM StudyMaterial sm WHERE sm.lesson.id = :lessonId ORDER BY sm.uploadedAt DESC")
    List<StudyMaterial> findByLessonIdOrderByUploadedAtDesc(@Param("lessonId") Long lessonId);
}