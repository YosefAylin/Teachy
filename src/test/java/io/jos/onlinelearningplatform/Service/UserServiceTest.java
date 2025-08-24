package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.Factory.UserFactory;
import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.model.Admin;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserFactory userFactory;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder, userFactory);

        // Default mock behavior
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
    }

    @Test
    @DisplayName("Register Student - Success using Factory")
    void register_StudentUsingFactory_Success() {
        logger.info("Testing student registration using factory");
        // Arrange
        RegisterDto registerDto = createRegisterDto("studentUser", "student@example.com", "password123", "STUDENT");
        Student mockStudent = new Student("studentUser", "student@example.com", "hashedPassword");
        mockStudent.setId(1L);

        when(userFactory.createUser(registerDto)).thenReturn(mockStudent);
        when(userRepository.save(any(User.class))).thenReturn(mockStudent);

        // Act
        User result = userService.register(registerDto);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Student.class, result);
        assertEquals("studentUser", result.getUsername());
        assertEquals("student@example.com", result.getEmail());
        assertTrue(((Student) result).isActive());

        verify(userFactory).createUser(any(RegisterDto.class));
        verify(userRepository).save(any(User.class));
        logger.info("Student registration test passed using factory");
    }

    @Test
    @DisplayName("Register Teacher - Success using Factory")
    void register_TeacherUsingFactory_Success() {
        logger.info("Testing teacher registration using factory");
        // Arrange
        RegisterDto registerDto = createRegisterDto("teacherUser", "teacher@example.com", "password123", "TEACHER");
        Teacher mockTeacher = new Teacher("teacherUser", "teacher@example.com", "hashedPassword");
        mockTeacher.setId(2L);
        mockTeacher.setActive(true);

        when(userFactory.createUser(registerDto)).thenReturn(mockTeacher);
        when(userRepository.save(any(User.class))).thenReturn(mockTeacher);

        // Act
        User result = userService.register(registerDto);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Teacher.class, result);
        assertEquals("teacherUser", result.getUsername());
        assertEquals("teacher@example.com", result.getEmail());
        assertTrue(((Teacher) result).isActive());

        verify(userFactory).createUser(any(RegisterDto.class));
        verify(userRepository).save(any(User.class));
        logger.info("Teacher registration test passed using factory");
    }

    @Test
    @DisplayName("Register Admin - Success using Factory")
    void register_AdminUsingFactory_Success() {
        logger.info("Testing admin registration using factory");
        // Arrange
        RegisterDto registerDto = createRegisterDto("adminUser", "admin@example.com", "password123", "ADMIN");
        Admin mockAdmin = new Admin("adminUser", "admin@example.com", "hashedPassword");
        mockAdmin.setId(3L);

        when(userFactory.createUser(registerDto)).thenReturn(mockAdmin);
        when(userRepository.save(any(User.class))).thenReturn(mockAdmin);

        // Act
        User result = userService.register(registerDto);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Admin.class, result);
        assertEquals("adminUser", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());

        verify(userFactory).createUser(any(RegisterDto.class));
        verify(userRepository).save(any(User.class));
        logger.info("Admin registration test passed using factory");
    }

    @Test
    @DisplayName("Register - Factory throws exception for invalid role")
    void register_InvalidRole_FactoryThrowsException() {
        logger.info("Testing registration with invalid role using factory");
        // Arrange
        RegisterDto registerDto = createRegisterDto("invalidUser", "invalid@example.com", "password123", "INVALID_ROLE");

        when(userFactory.createUser(registerDto)).thenThrow(new IllegalArgumentException("Unknown role: INVALID_ROLE"));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.register(registerDto)
        );
        assertTrue(exception.getMessage().contains("Unknown role"));

        verify(userFactory).createUser(any(RegisterDto.class));
        verify(userRepository, never()).save(any(User.class));
        logger.info("Invalid role registration test passed using factory");
    }

    @Test
    @DisplayName("Factory creates Student with correct properties")
    void factory_CreateStudent_CorrectProperties() {
        logger.info("Testing factory creates student with correct properties");
        // Arrange
        RegisterDto registerDto = createRegisterDto("studentTest", "student@test.com", "password123", "STUDENT");
        Student mockStudent = new Student("studentTest", "student@test.com", "hashedPassword");
        mockStudent.setActive(true);

        when(userFactory.createUser(registerDto)).thenReturn(mockStudent);
        when(userRepository.save(any(User.class))).thenReturn(mockStudent);

        // Act
        User result = userService.register(registerDto);

        // Assert
        assertInstanceOf(Student.class, result);
        Student student = (Student) result;
        assertEquals("studentTest", student.getUsername());
        assertEquals("student@test.com", student.getEmail());
        assertTrue(student.isActive());
        assertFalse(student.isConnected()); // Should be false by default

        logger.info("Factory student creation test passed");
    }

    @Test
    @DisplayName("Factory creates Teacher with correct properties")
    void factory_CreateTeacher_CorrectProperties() {
        logger.info("Testing factory creates teacher with correct properties");
        // Arrange
        RegisterDto registerDto = createRegisterDto("teacherTest", "teacher@test.com", "password123", "TEACHER");
        Teacher mockTeacher = new Teacher("teacherTest", "teacher@test.com", "hashedPassword");
        mockTeacher.setActive(true);

        when(userFactory.createUser(registerDto)).thenReturn(mockTeacher);
        when(userRepository.save(any(User.class))).thenReturn(mockTeacher);

        // Act
        User result = userService.register(registerDto);

        // Assert
        assertInstanceOf(Teacher.class, result);
        Teacher teacher = (Teacher) result;
        assertEquals("teacherTest", teacher.getUsername());
        assertEquals("teacher@test.com", teacher.getEmail());
        assertTrue(teacher.isActive());
        assertFalse(teacher.isConnected()); // Should be false by default
        assertNotNull(teacher.getMeetings()); // Should have initialized list

        logger.info("Factory teacher creation test passed");
    }

    @Test
    @DisplayName("View Profile - Success for existing user")
    void viewProfile_ExistingUser_Success() {
        logger.info("Testing view profile for existing user");
        // Arrange
        Student mockUser = new Student("testUser", "test@example.com", "hashedPassword");
        mockUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.viewProfile(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        logger.info("View profile test passed for user: {}", result.getUsername());
    }

    @Test
    @DisplayName("View Profile - Fails for non-existing user")
    void viewProfile_NonExistingUser_ThrowsException() {
        logger.info("Testing view profile for non-existing user");
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            userService.viewProfile(999L)
        );
        assertTrue(exception.getMessage().contains("User not found"));
        logger.info("Non-existing user profile test passed");
    }

    @Test
    @DisplayName("Change Password - Success with valid old password")
    void changePassword_ValidOldPassword_Success() {
        logger.info("Testing change password with valid old password");
        // Arrange
        Student mockUser = new Student("testUser", "test@example.com", "hashedOldPass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), eq("hashedOldPass"))).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedNewPass");

        // Act
        Boolean result = userService.changePassword(1L, "oldPass", "newPass");

        // Assert
        assertTrue(result);
        verify(passwordEncoder).encode(eq("newPass"));
        verify(userRepository).save(mockUser);
        logger.info("Change password test passed with valid old password");
    }

    @Test
    @DisplayName("Change Password - Fails with invalid old password")
    void changePassword_InvalidOldPassword_ThrowsException() {
        logger.info("Testing change password with invalid old password");
        // Arrange
        Student mockUser = new Student("testUser", "test@example.com", "hashedPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            userService.changePassword(1L, "wrongOldPass", "newPass")
        );
        assertTrue(exception.getMessage().contains("Old password is incorrect"));
        verify(userRepository, never()).save(any(User.class));
        logger.info("Invalid old password test passed");
    }

    @Test
    @DisplayName("Delete User - Success for existing user")
    void deleteUser_ExistingUser_Success() {
        logger.info("Testing delete user for existing user");
        // Arrange
        Student mockUser = new Student("testUser", "test@example.com", "hashedPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).delete(mockUser);
        logger.info("Delete user test passed for existing user");
    }

    @Test
    @DisplayName("Delete User - Fails for non-existing user")
    void deleteUser_NonExistingUser_ThrowsException() {
        logger.info("Testing delete user for non-existing user");
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            userService.deleteUser(999L)
        );
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, never()).delete(any(User.class));
        logger.info("Non-existing user deletion test passed");
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
