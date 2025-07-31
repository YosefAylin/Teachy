package io.jos.onlinelearningplatform.config;

import io.jos.onlinelearningplatform.model.Admin;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                            : u instanceof Teacher ? "TEACHER"
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
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/home",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/login",      // <–– make sure your login page is permitted
                                "/register"    // <–– if you have a register page
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")       // <–– point to your (or Spring's default) login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}