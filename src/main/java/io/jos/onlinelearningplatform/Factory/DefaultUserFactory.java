package io.jos.onlinelearningplatform.Factory;

import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DefaultUserFactory implements UserFactory {

    private final PasswordEncoder encoder;

    public DefaultUserFactory(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public User createUser(RegisterDto dto) {
        String role = dto.getRole().toUpperCase(); // כי הוא String
        String enc = encoder.encode(dto.getPassword());

        return switch (role) {
            case "ADMIN" -> new Admin(dto.getUsername(), dto.getEmail(), enc);
            case "TEACHER" -> {
                Teacher t = new Teacher(dto.getUsername(), dto.getEmail(), enc);
                t.setActive(true);
                yield t;
            }
            case "STUDENT" -> {
                Student s = new Student(dto.getUsername(), dto.getEmail(), enc);
                s.setActive(true);
                yield s;
            }
            default -> throw new IllegalArgumentException("Unknown role: " + dto.getRole());
        };
    }
}