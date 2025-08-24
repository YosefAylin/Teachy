package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.model.User;

public interface UserService {
    User register(RegisterDto dto  );
    void deleteUser(Long userId);
    User viewProfile(Long userId);
    Boolean changePassword(Long userId, String oldPassword, String newPassword);
}
