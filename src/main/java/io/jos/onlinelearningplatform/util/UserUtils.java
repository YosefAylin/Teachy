package io.jos.onlinelearningplatform.util;

import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    private final UserRepository userRepository;

    public UserUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getCurrentStudentId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + username));

        if (!(user instanceof Student)) {
            throw new IllegalStateException("Logged-in user is not a STUDENT: " + username);
        }

        return user.getId();
    }

    public Long getCurrentTeacherId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + username));

        if (!(user instanceof Teacher)) {
            throw new IllegalStateException("Logged-in user is not a TEACHER: " + username);
        }

        return user.getId();
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + username));
    }
}
