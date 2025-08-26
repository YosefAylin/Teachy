package io.jos.onlinelearningplatform.factory;

import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.dto.RegisterDto;

public interface UserFactory {
    User createUser(RegisterDto dto);
}