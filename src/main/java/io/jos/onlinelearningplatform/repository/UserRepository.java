package io.jos.onlinelearningplatform.repository;
import io.jos.onlinelearningplatform.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);


    List<User> findTopByOrderByIdDesc(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE TYPE(u) = :userType")
    int countByUserType(@Param("userType") Class<?> userType);

}

