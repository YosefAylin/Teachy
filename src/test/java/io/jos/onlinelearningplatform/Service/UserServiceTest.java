package io.jos.onlinelearningplatform.Service;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.StudentService;
import io.jos.onlinelearningplatform.service.TeacherService;
import io.jos.onlinelearningplatform.service.UserService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @BeforeEach
    public void setUp() {
        studentService.registerUser("Yosef Aylin", "password123", "aba@gas.com");
        studentService.registerUser("Ashley", "password123", "eam@exam.com");
        teacherService.registerUser("Joseph", "password123", "asdd@dsd.com");
        teacherService.registerUser("Aylin", "password123", "dsa@sd.com");
        System.out.println("testUsersRegister PASSED");

    }

    @Test
    public void testUsersRegisterWithInvalidData() {
        try {
            // this one should throw
            studentService.registerUser("", "password123", "dsad.casd.com");
            System.out.println("testUsersRegisterWithInvalidData FAILED → no exception");
            fail("Expected IllegalArgumentException on invalid registration");
        } catch (IllegalArgumentException e) {
            System.out.println("testUsersRegisterWithInvalidData PASSED → caught IllegalArgumentException");
        }

        userRepository.deleteAll();
    }

    @Test
    public void testLoginWithValidCredentials() {
        userService.loginUser("Yosef Aylin", "password123");
        userService.loginUser("Joseph", "password123");
        System.out.println("testLoginWithValidCredentials PASSED");

        userRepository.deleteAll();

    }

    @Test
    public void testLoginWithInvalidCredentials() {  try {
        // should throw for bad password
        userService.loginUser("Joseph", "wrongpass");
        System.out.println("testLoginWithInvalidCredentials FAILED → no exception");
        fail("Expected IllegalArgumentException on bad login");
    } catch (IllegalArgumentException ex) {
        System.out.println("testLoginWithInvalidCredentials PASSED → caught IllegalArgumentException");
        }
        userRepository.deleteAll();

    }


    @Test
    public void testLogoutUser() {
        userService.loginUser("Yosef Aylin", "password123");
        userService.loginUser("Joseph", "password123");
        userService.logoutUser("Yosef Aylin"); // Assuming user with ID 1 exists
        userService.logoutUser("Joseph"); // Assuming user with ID 2 exists

        userRepository.deleteAll();

    }
}
