package io.jos.onlinelearningplatform.config;

import io.jos.onlinelearningplatform.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();

        // Update user's connected status in database
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setConnected(true);
            userRepository.save(user);
            logger.info("User '{}' connected status updated to true", username);
        });

        // Redirect to home page
        response.sendRedirect("/home");
    }
}
