package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.StudyMaterial;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.StudyMaterialRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.StudyMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class StudyMaterialServiceImpl implements StudyMaterialService {

    private static final Logger logger = LoggerFactory.getLogger(StudyMaterialServiceImpl.class);

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
        logger.debug("Uploading material for lesson ID: {}, uploader ID: {}, filename: {}",
                    lessonId, uploaderId, file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("Attempted to upload empty file for lesson ID: {}", lessonId);
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

            StudyMaterial savedMaterial = studyMaterialRepository.save(material);
            logger.info("Successfully uploaded study material with ID: {} for lesson: {}, filename: {}, size: {} bytes",
                       savedMaterial.getId(), lessonId, file.getOriginalFilename(), file.getSize());
            return savedMaterial;
        } catch (IOException e) {
            logger.error("Failed to upload file {} for lesson ID: {}", file.getOriginalFilename(), lessonId, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public List<StudyMaterial> getMaterialsByLesson(Long lessonId) {
        logger.debug("Getting materials for lesson ID: {}", lessonId);
        List<StudyMaterial> materials = studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(lessonId);
        logger.info("Found {} materials for lesson ID: {}", materials.size(), lessonId);
        return materials;
    }

    @Override
    public void deleteMaterial(Long materialId) {
        logger.debug("Deleting study material with ID: {}", materialId);

        // First validate that material exists - this is what the test expects
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found"));

        try {
            // Use delete(entity) instead of deleteById() to match test expectations
            studyMaterialRepository.delete(material);
            logger.info("Successfully deleted study material with ID: {} (filename: {})", materialId, material.getFileName());
        } catch (Exception e) {
            logger.error("Error deleting study material with ID: {}", materialId, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadMaterial(Long materialId) {
        logger.debug("Downloading study material with ID: {}", materialId);

        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found"));

        if (material.getFileData() == null || material.getFileData().length == 0) {
            logger.warn("File data not available for material ID: {}", materialId);
            throw new IllegalStateException("File data not available for material ID: " + materialId);
        }

        logger.info("Successfully retrieved file data for material: {} (size: {} bytes)",
                   material.getFileName(), material.getFileData().length);
        return material.getFileData();
    }

    @Override
    public StudyMaterial getMaterialById(Long materialId) {
        logger.debug("Fetching study material with ID: {}", materialId);
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found"));
        logger.info("Found study material: {} (uploaded by: {})", material.getFileName(), material.getUploader().getUsername());
        return material;
    }
}