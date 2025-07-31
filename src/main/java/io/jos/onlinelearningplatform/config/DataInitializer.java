//package io.jos.onlinelearningplatform.config;
//
//import io.jos.onlinelearningplatform.model.Student;
//import io.jos.onlinelearningplatform.model.Teacher;
//import io.jos.onlinelearningplatform.service.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//
//@Configuration
//public class DataInitializer {
//    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
//
//    @Bean
//    @Order(2) // Run after AdminInitializer
//    CommandLineRunner initDatabase(UserService userService) {
//        return args -> {
//            // Create Students
//            createStudent(userService, "alice", "alice@student.com");
//            createStudent(userService, "bob", "bob@student.com");
//            createStudent(userService, "charlie", "charlie@student.com");
//            createStudent(userService, "david", "david@student.com");
//            createStudent(userService, "emma", "emma@student.com");
//            createStudent(userService, "frank", "frank@student.com");
//            createStudent(userService, "grace", "grace@student.com");
//            createStudent(userService, "henry", "henry@student.com");
//            createStudent(userService, "isabel", "isabel@student.com");
//            createStudent(userService, "jack", "jack@student.com");
//
//            // Create Teachers
//            createTeacher(userService, "smith", "smith@teacher.com");
//            createTeacher(userService, "johnson", "johnson@teacher.com");
//            createTeacher(userService, "williams", "williams@teacher.com");
//            createTeacher(userService, "brown", "brown@teacher.com");
//            createTeacher(userService, "jones", "jones@teacher.com");
//            createTeacher(userService, "garcia", "garcia@teacher.com");
//            createTeacher(userService, "miller", "miller@teacher.com");
//            createTeacher(userService, "davis", "davis@teacher.com");
//            createTeacher(userService, "rodriguez", "rodriguez@teacher.com");
//            createTeacher(userService, "martinez", "martinez@teacher.com");
//
//            logger.info("Sample users initialized successfully");
//        };
//    }
//
//    private void createStudent(UserService userService, String username, String email) {
//        try {
//            userService.register(username, email, username + "123", "STUDENT");
//            logger.info("Created student: {}", username);
//        } catch (Exception e) {
//            logger.warn("Could not create student {}: {}", username, e.getMessage());
//        }
//    }
//
//    private void createTeacher(UserService userService, String username, String email) {
//        try {
//            userService.register(username, email, username + "123", "TEACHER");
//            logger.info("Created teacher: {}", username);
//        } catch (Exception e) {
//            logger.warn("Could not create teacher {}: {}", username, e.getMessage());
//        }
//    }
//}
