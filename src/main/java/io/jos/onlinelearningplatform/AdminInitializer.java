package io.jos.onlinelearningplatform;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);
    private final AdminService adminService;
    private final UserRepository userRepository;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminEmail;

    public AdminInitializer(
            AdminService adminService,
            UserRepository userRepository,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword,
            @Value("${app.admin.email}")    String adminEmail
    ) {
        this.adminService    = adminService;
        this.userRepository  = userRepository;
        this.adminUsername   = adminUsername;
        this.adminPassword   = adminPassword;
        this.adminEmail      = adminEmail;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // You might check “does admin already exist?” to avoid duplicates
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            adminService.registerUser(adminUsername, adminPassword, adminEmail);
            log.info("✅ Bootstrap: created initial ADMIN user '{}'", adminUsername);
        } else {
            log.info("⚠️ Bootstrap: ADMIN user '{}' already exists, skipping creation", adminUsername);
        }
    }
}
