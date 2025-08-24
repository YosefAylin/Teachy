package io.jos.onlinelearningplatform.Factory;

import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.model.Admin;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger(UserFactoryTest.class);

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userFactory = new DefaultUserFactory(passwordEncoder);

        // Mock password encoding
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
    }

    @Test
    @DisplayName("Factory creates Student with correct type and properties")
    void createUser_StudentRole_ReturnsStudentInstance() {
        logger.info("Testing factory creates Student instance");
        // Arrange
        RegisterDto dto = createRegisterDto("studentUser", "student@example.com", "password123", "STUDENT");

        // Act
        User result = userFactory.createUser(dto);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Student.class, result);
        assertEquals("studentUser", result.getUsername());
        assertEquals("student@example.com", result.getEmail());
        assertEquals("hashedPassword123", result.getPasswordHash());
        assertTrue(((Student) result).isActive()); // Should be set to true by factory
        assertFalse(result.isConnected()); // Default value

        logger.info("Factory Student creation test passed");
    }

    @Test
    @DisplayName("Factory creates Teacher with correct type and properties")
    void createUser_TeacherRole_ReturnsTeacherInstance() {
        logger.info("Testing factory creates Teacher instance");
        // Arrange
        RegisterDto dto = createRegisterDto("teacherUser", "teacher@example.com", "password123", "TEACHER");

        // Act
        User result = userFactory.createUser(dto);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Teacher.class, result);
        assertEquals("teacherUser", result.getUsername());
        assertEquals("teacher@example.com", result.getEmail());
        assertEquals("hashedPassword123", result.getPasswordHash());
        assertTrue(((Teacher) result).isActive()); // Should be set to true by factory
        assertFalse(result.isConnected()); // Default value
        assertNotNull(((Teacher) result).getMeetings()); // Should have initialized list
        assertTrue(((Teacher) result).getMeetings().isEmpty()); // Should be empty initially

        logger.info("Factory Teacher creation test passed");
    }

    @Test
    @DisplayName("Factory creates Admin with correct type and properties")
    void createUser_AdminRole_ReturnsAdminInstance() {
        logger.info("Testing factory creates Admin instance");
        // Arrange
        RegisterDto dto = createRegisterDto("adminUser", "admin@example.com", "password123", "ADMIN");

        // Act
        User result = userFactory.createUser(dto);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Admin.class, result);
        assertEquals("adminUser", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("hashedPassword123", result.getPasswordHash());
        assertTrue(result.isActive()); // Default value should be true
        assertFalse(result.isConnected()); // Default value

        logger.info("Factory Admin creation test passed");
    }

    @Test
    @DisplayName("Factory handles case-insensitive roles")
    void createUser_CaseInsensitiveRoles_WorksCorrectly() {
        logger.info("Testing factory handles case-insensitive roles");

        // Test lowercase
        RegisterDto lowerDto = createRegisterDto("user1", "user1@example.com", "password123", "student");
        User lowerResult = userFactory.createUser(lowerDto);
        assertInstanceOf(Student.class, lowerResult);

        // Test mixed case
        RegisterDto mixedDto = createRegisterDto("user2", "user2@example.com", "password123", "TeAcHeR");
        User mixedResult = userFactory.createUser(mixedDto);
        assertInstanceOf(Teacher.class, mixedResult);

        // Test uppercase
        RegisterDto upperDto = createRegisterDto("user3", "user3@example.com", "password123", "ADMIN");
        User upperResult = userFactory.createUser(upperDto);
        assertInstanceOf(Admin.class, upperResult);

        logger.info("Case-insensitive role test passed");
    }

    @Test
    @DisplayName("Factory throws exception for invalid role")
    void createUser_InvalidRole_ThrowsIllegalArgumentException() {
        logger.info("Testing factory throws exception for invalid role");
        // Arrange
        RegisterDto dto = createRegisterDto("invalidUser", "invalid@example.com", "password123", "INVALID_ROLE");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userFactory.createUser(dto);
        });

        assertTrue(exception.getMessage().contains("Unknown role: INVALID_ROLE"));
        logger.info("Invalid role exception test passed");
    }

    @Test
    @DisplayName("Factory throws exception for null role")
    void createUser_NullRole_ThrowsException() {
        logger.info("Testing factory throws exception for null role");
        // Arrange
        RegisterDto dto = createRegisterDto("nullUser", "null@example.com", "password123", null);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userFactory.createUser(dto);
        });

        logger.info("Null role exception test passed");
    }

    @Test
    @DisplayName("Factory properly encodes password")
    void createUser_PasswordEncoding_WorksCorrectly() {
        logger.info("Testing factory properly encodes password");
        // Arrange
        String originalPassword = "mySecretPassword123";
        when(passwordEncoder.encode(originalPassword)).thenReturn("speciallyHashedPassword");

        RegisterDto dto = createRegisterDto("user", "user@example.com", originalPassword, "STUDENT");

        // Act
        User result = userFactory.createUser(dto);

        // Assert
        assertEquals("speciallyHashedPassword", result.getPasswordHash());
        assertNotEquals(originalPassword, result.getPasswordHash()); // Should not store plain text

        logger.info("Password encoding test passed");
    }

    @Test
    @DisplayName("Factory creates different instances for same input")
    void createUser_MultipleCreations_ReturnsDifferentInstances() {
        logger.info("Testing factory creates different instances");
        // Arrange
        RegisterDto dto1 = createRegisterDto("user1", "user1@example.com", "password123", "STUDENT");
        RegisterDto dto2 = createRegisterDto("user2", "user2@example.com", "password123", "STUDENT");

        // Act
        User result1 = userFactory.createUser(dto1);
        User result2 = userFactory.createUser(dto2);

        // Assert
        assertNotSame(result1, result2); // Different instances
        assertInstanceOf(Student.class, result1);
        assertInstanceOf(Student.class, result2);
        assertNotEquals(result1.getUsername(), result2.getUsername());

        logger.info("Multiple instance creation test passed");
    }

    @Test
    @DisplayName("Factory maintains data integrity across different user types")
    void createUser_DataIntegrity_MaintainedAcrossTypes() {
        logger.info("Testing factory maintains data integrity across types");
        // Arrange & Act
        User student = userFactory.createUser(createRegisterDto("student", "s@test.com", "pass1", "STUDENT"));
        User teacher = userFactory.createUser(createRegisterDto("teacher", "t@test.com", "pass2", "TEACHER"));
        User admin = userFactory.createUser(createRegisterDto("admin", "a@test.com", "pass3", "ADMIN"));

        // Assert
        // Each should maintain their own data
        assertEquals("student", student.getUsername());
        assertEquals("teacher", teacher.getUsername());
        assertEquals("admin", admin.getUsername());

        assertEquals("s@test.com", student.getEmail());
        assertEquals("t@test.com", teacher.getEmail());
        assertEquals("a@test.com", admin.getEmail());

        // All should have proper inheritance
        assertInstanceOf(Student.class, student);
        assertInstanceOf(Teacher.class, teacher);
        assertInstanceOf(Admin.class, admin);

        logger.info("Data integrity test passed");
    }

    // Helper method to create RegisterDto
    private RegisterDto createRegisterDto(String username, String email, String password, String role) {
        RegisterDto dto = new RegisterDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(role);
        return dto;
    }
}
