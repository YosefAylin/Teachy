package io.jos.onlinelearningplatform.config;

import io.jos.onlinelearningplatform.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    private final UserRepository userRepository;

    public CustomLogoutSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                               Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            String username = authentication.getName();

            // Update user's connected status in database
            userRepository.findByUsername(username).ifPresent(user -> {
                user.setConnected(false);
                userRepository.save(user);
                logger.info("User '{}' connected status updated to false", username);
            });
        }

        // Redirect to login page with logout message
        response.sendRedirect("/login?logout");
    }
}
