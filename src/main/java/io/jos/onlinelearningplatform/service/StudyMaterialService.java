package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.StudyMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudyMaterialService {
    StudyMaterial uploadMaterial(Long lessonId, Long uploaderId, MultipartFile file, String description);
    List<StudyMaterial> getMaterialsByLesson(Long lessonId);
    StudyMaterial getMaterialById(Long materialId);
    void deleteMaterial(Long materialId);
    byte[] downloadMaterial(Long materialId);
}