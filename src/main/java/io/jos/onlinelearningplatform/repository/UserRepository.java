package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
