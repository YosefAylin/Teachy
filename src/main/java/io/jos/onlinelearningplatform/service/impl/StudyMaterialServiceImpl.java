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
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StudyMaterialServiceImpl.class);

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
    public void deleteMaterial(Long materialId) {
        logger.info("Deleting study material with ID: {}", materialId);

        // First validate that material exists - this is what the test expects
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found"));

        try {
            // Use delete(entity) instead of deleteById() to match test expectations
            studyMaterialRepository.delete(material);
            logger.info("Successfully deleted study material with ID: {}", materialId);
        } catch (Exception e) {
            logger.error("Error deleting study material with ID: {}", materialId, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadMaterial(Long materialId) {
        logger.info("Downloading study material with ID: {}", materialId);

        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found"));

        if (material.getFileData() == null || material.getFileData().length == 0) {
            throw new IllegalStateException("File data not available for material ID: " + materialId);
        }

        logger.info("Successfully retrieved file data for material: {}", material.getFileName());
        return material.getFileData();
    }

    @Override
    public StudyMaterial getMaterialById(Long materialId) {
        logger.info("Fetching study material with ID: {}", materialId);

        return studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found"));
    }
}