package io.jos.onlinelearningplatform;

import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OnlineLearningPlatformApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Mock
    @Test
    void testSaveUser() {
        User user = new Student(); // or just `new User()` if abstract isn't used
        user.setUsername("testuser");
        user.setEmail("test@email.com");
//        user.setPassword("test123");
//        user.setRole("STUDENT");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
    }
    @Test
    void contextLoads() {
    }


}
