package io.jos.onlinelearningplatform.facade;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class MainFacade {

    public String getHomePage() {
        return "home";
    }

    public String redirectByRole(Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        if (hasRole(auth, "ROLE_ADMIN")) {
            return "redirect:/admin/home";
        }
        if (hasRole(auth, "ROLE_TEACHER")) {
            return "redirect:/teacher/home";
        }
        if (hasRole(auth, "ROLE_STUDENT")) {
            return "redirect:/student/home";
        }

        return "redirect:/login?error";
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
