package io.jos.onlinelearningplatform.Factory;

import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.dto.RegisterDto;

public interface UserFactory {
    User createUser(RegisterDto dto);
}