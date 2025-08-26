package io.jos.onlinelearningplatform.repository;
import io.jos.onlinelearningplatform.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u ORDER BY u.id DESC")
    List<User> findTopByOrderByIdDesc(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE TYPE(u) = :userType")
    int countByUserType(@Param("userType") Class<?> userType);
}
