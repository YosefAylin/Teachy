package io.jos.onlinelearningplatform;

import io.jos.onlinelearningplatform.model.Admin;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .map(u -> {
                    // figure out which subtype this is
                    String role = u instanceof Admin   ? "ADMIN"
                            : u instanceof Student ? "STUDENT"
                            :                         "UNKNOWN";

                    // now build the UserDetails
                    return org.springframework.security.core.userdetails.User
                            .withUsername(u.getUsername())
                            .password(u.getPasswordHash())
                            .roles(role)   // use the computed role here
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
