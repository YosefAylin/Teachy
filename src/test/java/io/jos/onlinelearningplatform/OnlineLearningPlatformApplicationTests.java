package io.jos.onlinelearningplatform;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = "app.seed-admin.enabled=false")
class OnlineLearningPlatformApplicationTests {

    // This test will simply check if the application context loads successfully
    @Test
    void contextLoads() {
        // No implementation needed, just checking if the context loads
    }

}
