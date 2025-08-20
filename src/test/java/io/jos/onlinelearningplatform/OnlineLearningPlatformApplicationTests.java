package io.jos.onlinelearningplatform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "app.seed-admin.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    }
)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class OnlineLearningPlatformApplicationTests {

    @Test
    void contextLoads() {
        // This test will simply check if the application context loads successfully
        // No implementation needed, just checking if the context loads
    }
}
