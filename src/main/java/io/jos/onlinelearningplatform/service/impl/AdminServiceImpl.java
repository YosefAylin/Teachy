package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void manageUsers() {

    }

    @Override
    public void viewFeedback() {

    }
}
