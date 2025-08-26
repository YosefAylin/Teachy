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
    private final RegisterDto dto;

    public AdminInitializer(
            UserService userService,
            UserRepository userRepository,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword,
            @Value("${app.admin.email}")    String adminEmail
    ) {
        this.userService    = userService;
        this.userRepository  = userRepository;
        this.dto = new RegisterDto();
        this.dto.setUsername(adminUsername);
        this.dto.setPassword(adminPassword);
        this.dto.setEmail(adminEmail);
        this.dto.setRole("ADMIN");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // You might check "does admin already exist?" to avoid duplicates
        if (userRepository.findByUsername(dto.getUsername()).isEmpty()) {
            userService.register(dto);
            log.info("✅ Bootstrap: created initial ADMIN user '{}'", dto.getUsername());
        } else {
            log.info("⚠️ Bootstrap: ADMIN user '{}' already exists, skipping creation", dto.getUsername());
        }

    }
}