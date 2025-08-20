package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findByLessonIdOrderByUploadedAtDesc(Long lessonId);
}