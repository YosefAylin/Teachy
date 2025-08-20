package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.StudyMaterial;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.StudyMaterialRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.StudyMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class StudyMaterialServiceImpl implements StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public StudyMaterialServiceImpl(StudyMaterialRepository studyMaterialRepository,
                                   LessonRepository lessonRepository,
                                   UserRepository userRepository) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    @Override
    public StudyMaterial uploadMaterial(Long lessonId, Long uploaderId, MultipartFile file, String description) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            StudyMaterial material = new StudyMaterial();
            material.setLesson(lesson);
            material.setUploader(uploader);
            material.setFileName(file.getOriginalFilename());
            material.setFileSize(file.getSize());
            material.setDescription(description);
            material.setFileData(file.getBytes());

            return studyMaterialRepository.save(material);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public List<StudyMaterial> getMaterialsByLesson(Long lessonId) {
        return studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(lessonId);
    }

    @Override
    public StudyMaterial getMaterialById(Long materialId) {
        return studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));
    }

    @Override
    public void deleteMaterial(Long materialId) {
        studyMaterialRepository.deleteById(materialId);
    }

    @Override
    public byte[] downloadMaterial(Long materialId) {
        StudyMaterial material = getMaterialById(materialId);
        return material.getFileData();
    }
}