package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.StudyMaterial;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.StudyMaterialRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.impl.StudyMaterialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudyMaterialServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(StudyMaterialServiceTest.class);

    @Mock
    private StudyMaterialRepository studyMaterialRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile mockFile;

    private StudyMaterialService studyMaterialService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studyMaterialService = new StudyMaterialServiceImpl(studyMaterialRepository, lessonRepository, userRepository);
        logger.info("StudyMaterialServiceTest setup completed");
    }

    @Test
    @DisplayName("Upload Material - Success")
    void uploadMaterial_ValidData_Success() throws IOException {
        logger.info("Testing uploadMaterial with valid data");
        // Arrange
        Long lessonId = 1L;
        Long uploaderId = 2L;
        String description = "Test material";
        byte[] fileData = "test content".getBytes();

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        User uploader = new User();
        uploader.setId(uploaderId);
        StudyMaterial savedMaterial = new StudyMaterial();
        savedMaterial.setId(1L);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn(fileData);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findById(uploaderId)).thenReturn(Optional.of(uploader));
        when(studyMaterialRepository.save(any(StudyMaterial.class))).thenReturn(savedMaterial);

        // Act
        StudyMaterial result = studyMaterialService.uploadMaterial(lessonId, uploaderId, mockFile, description);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(lessonRepository).findById(lessonId);
        verify(userRepository).findById(uploaderId);
        verify(studyMaterialRepository).save(any(StudyMaterial.class));
        logger.info("uploadMaterial success test passed");
    }

    @Test
    @DisplayName("Upload Material - Empty File")
    void uploadMaterial_EmptyFile_ThrowsException() {
        logger.info("Testing uploadMaterial with empty file");
        // Arrange
        Long lessonId = 1L;
        Long uploaderId = 2L;
        String description = "Test material";

        when(mockFile.isEmpty()).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> studyMaterialService.uploadMaterial(lessonId, uploaderId, mockFile, description));

        assertEquals("File cannot be empty", exception.getMessage());
        verify(studyMaterialRepository, never()).save(any(StudyMaterial.class));
        logger.info("uploadMaterial empty file test passed");
    }

    @Test
    @DisplayName("Upload Material - Lesson Not Found")
    void uploadMaterial_LessonNotFound_ThrowsException() {
        logger.info("Testing uploadMaterial with non-existent lesson");
        // Arrange
        Long lessonId = 999L;
        Long uploaderId = 2L;
        String description = "Test material";

        when(mockFile.isEmpty()).thenReturn(false);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> studyMaterialService.uploadMaterial(lessonId, uploaderId, mockFile, description));

        assertEquals("Lesson not found", exception.getMessage());
        verify(lessonRepository).findById(lessonId);
        verify(studyMaterialRepository, never()).save(any(StudyMaterial.class));
        logger.info("uploadMaterial lesson not found test passed");
    }

    @Test
    @DisplayName("Upload Material - User Not Found")
    void uploadMaterial_UserNotFound_ThrowsException() {
        logger.info("Testing uploadMaterial with non-existent user");
        // Arrange
        Long lessonId = 1L;
        Long uploaderId = 999L;
        String description = "Test material";

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);

        when(mockFile.isEmpty()).thenReturn(false);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findById(uploaderId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> studyMaterialService.uploadMaterial(lessonId, uploaderId, mockFile, description));

        assertEquals("User not found", exception.getMessage());
        verify(lessonRepository).findById(lessonId);
        verify(userRepository).findById(uploaderId);
        verify(studyMaterialRepository, never()).save(any(StudyMaterial.class));
        logger.info("uploadMaterial user not found test passed");
    }

    @Test
    @DisplayName("Upload Material - IOException")
    void uploadMaterial_IOException_ThrowsRuntimeException() throws IOException {
        logger.info("Testing uploadMaterial with IOException");
        // Arrange
        Long lessonId = 1L;
        Long uploaderId = 2L;
        String description = "Test material";

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        User uploader = new User();
        uploader.setId(uploaderId);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenThrow(new IOException("File read error"));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findById(uploaderId)).thenReturn(Optional.of(uploader));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> studyMaterialService.uploadMaterial(lessonId, uploaderId, mockFile, description));

        assertEquals("Failed to upload file", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
        verify(studyMaterialRepository, never()).save(any(StudyMaterial.class));
        logger.info("uploadMaterial IOException test passed");
    }

    @Test
    @DisplayName("Get Materials By Lesson - Success")
    void getMaterialsByLesson_Success() {
        logger.info("Testing getMaterialsByLesson");
        // Arrange
        Long lessonId = 1L;
        StudyMaterial material1 = new StudyMaterial();
        material1.setFileName("material1.pdf");
        StudyMaterial material2 = new StudyMaterial();
        material2.setFileName("material2.docx");
        List<StudyMaterial> expectedMaterials = Arrays.asList(material1, material2);

        when(studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(lessonId)).thenReturn(expectedMaterials);

        // Act
        List<StudyMaterial> result = studyMaterialService.getMaterialsByLesson(lessonId);

        // Assert
        assertEquals(2, result.size());
        assertEquals("material1.pdf", result.get(0).getFileName());
        assertEquals("material2.docx", result.get(1).getFileName());
        verify(studyMaterialRepository).findByLessonIdOrderByUploadedAtDesc(lessonId);
        logger.info("getMaterialsByLesson test passed");
    }

    @Test
    @DisplayName("Get Materials By Lesson - Empty Result")
    void getMaterialsByLesson_EmptyResult() {
        logger.info("Testing getMaterialsByLesson with empty result");
        // Arrange
        Long lessonId = 1L;
        when(studyMaterialRepository.findByLessonIdOrderByUploadedAtDesc(lessonId)).thenReturn(Arrays.asList());

        // Act
        List<StudyMaterial> result = studyMaterialService.getMaterialsByLesson(lessonId);

        // Assert
        assertTrue(result.isEmpty());
        verify(studyMaterialRepository).findByLessonIdOrderByUploadedAtDesc(lessonId);
        logger.info("getMaterialsByLesson empty result test passed");
    }

    @Test
    @DisplayName("Get Material By ID - Success")
    void getMaterialById_Success() {
        logger.info("Testing getMaterialById");
        // Arrange
        Long materialId = 1L;
        StudyMaterial material = new StudyMaterial();
        material.setId(materialId);
        material.setFileName("test.pdf");

        when(studyMaterialRepository.findById(materialId)).thenReturn(Optional.of(material));

        // Act
        StudyMaterial result = studyMaterialService.getMaterialById(materialId);

        // Assert
        assertNotNull(result);
        assertEquals(materialId, result.getId());
        assertEquals("test.pdf", result.getFileName());
        verify(studyMaterialRepository).findById(materialId);
        logger.info("getMaterialById test passed");
    }

    @Test
    @DisplayName("Get Material By ID - Material Not Found")
    void getMaterialById_MaterialNotFound_ThrowsException() {
        logger.info("Testing getMaterialById with non-existent material");
        // Arrange
        Long materialId = 999L;
        when(studyMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> studyMaterialService.getMaterialById(materialId));

        assertEquals("Study material not found", exception.getMessage());
        verify(studyMaterialRepository).findById(materialId);
        logger.info("getMaterialById material not found test passed");
    }

    @Test
    @DisplayName("Delete Material - Success")
    void deleteMaterial_Success() {
        logger.info("Testing deleteMaterial");
        // Arrange
        Long materialId = 1L;
        StudyMaterial material = new StudyMaterial();
        material.setId(materialId);

        when(studyMaterialRepository.findById(materialId)).thenReturn(Optional.of(material));

        // Act
        assertDoesNotThrow(() -> studyMaterialService.deleteMaterial(materialId));

        // Assert
        verify(studyMaterialRepository).findById(materialId);
        verify(studyMaterialRepository).delete(material);
        logger.info("deleteMaterial test passed");
    }

    @Test
    @DisplayName("Delete Material - Material Not Found")
    void deleteMaterial_MaterialNotFound_ThrowsException() {
        logger.info("Testing deleteMaterial with non-existent material");
        // Arrange
        Long materialId = 999L;
        when(studyMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> studyMaterialService.deleteMaterial(materialId));

        assertEquals("Study material not found", exception.getMessage());
        verify(studyMaterialRepository).findById(materialId);
        verify(studyMaterialRepository, never()).delete(any(StudyMaterial.class));
        logger.info("deleteMaterial material not found test passed");
    }

    @Test
    @DisplayName("Delete Material - Exception During Delete")
    void deleteMaterial_ExceptionDuringDelete_ThrowsException() {
        logger.info("Testing deleteMaterial with exception during delete");
        // Arrange
        Long materialId = 1L;
        StudyMaterial material = new StudyMaterial();
        material.setId(materialId);
        RuntimeException deleteException = new RuntimeException("Database error");

        when(studyMaterialRepository.findById(materialId)).thenReturn(Optional.of(material));
        doThrow(deleteException).when(studyMaterialRepository).delete(material);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> studyMaterialService.deleteMaterial(materialId));

        assertEquals("Database error", exception.getMessage());
        verify(studyMaterialRepository).findById(materialId);
        verify(studyMaterialRepository).delete(material);
        logger.info("deleteMaterial exception during delete test passed");
    }
}
