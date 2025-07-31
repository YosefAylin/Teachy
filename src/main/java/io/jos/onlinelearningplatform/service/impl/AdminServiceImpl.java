package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void manageUsers() {
        logger.info("Admin accessing user management interface");
    }

    @Override
    public void viewFeedback() {
        logger.info("Admin viewing system feedback");
    }
}
