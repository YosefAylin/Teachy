package io.jos.onlinelearningplatform.config;

import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);
    private final UserService userService;
    private final UserRepository userRepository;

    // Secondary configurable admin
    private final String secondaryAdminUsername;
    private final String secondaryAdminPassword;
    private final String secondaryAdminEmail;
    private final boolean createSecondaryAdmin;

    public AdminInitializer(
            UserService userService,
            UserRepository userRepository,
            @Value("${app.admin.username:}") String adminUsername,
            @Value("${app.admin.password:}") String adminPassword,
            @Value("${app.admin.email:}")    String adminEmail,
            @Value("${app.admin.create-secondary:false}") boolean createSecondaryAdmin
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.secondaryAdminUsername = adminUsername;
        this.secondaryAdminPassword = adminPassword;
        this.secondaryAdminEmail = adminEmail;
        this.createSecondaryAdmin = createSecondaryAdmin;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Always create jos as primary admin
        createAdminUser("jos", "jos123", "jos@teachy.com", "primary");

        // Create secondary admin if configured
        if (createSecondaryAdmin && isValidSecondaryAdminConfig()) {
            createAdminUser(secondaryAdminUsername, secondaryAdminPassword, secondaryAdminEmail, "secondary");
        } else if (createSecondaryAdmin) {
            log.warn("⚠️ Bootstrap: Secondary admin creation enabled but configuration incomplete. " +
                    "Please set app.admin.username, app.admin.password, and app.admin.email");
        }
    }

    private void createAdminUser(String username, String password, String email, String type) {
        if (userRepository.findByUsername(username).isEmpty()) {
            RegisterDto dto = new RegisterDto();
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setEmail(email);
            dto.setRole("ADMIN");

            userService.register(dto);
            log.info("✅ Bootstrap: created {} ADMIN user '{}'", type, username);
        } else {
            log.info("⚠️ Bootstrap: {} ADMIN user '{}' already exists, skipping creation", type, username);
        }
    }

    private boolean isValidSecondaryAdminConfig() {
        return secondaryAdminUsername != null && !secondaryAdminUsername.trim().isEmpty() &&
               secondaryAdminPassword != null && !secondaryAdminPassword.trim().isEmpty() &&
               secondaryAdminEmail != null && !secondaryAdminEmail.trim().isEmpty();
    }
}