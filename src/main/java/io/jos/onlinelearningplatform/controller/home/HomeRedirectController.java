package io.jos.onlinelearningplatform.controller.home;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeRedirectController {
    
    @GetMapping("/home/redirect")
    public RedirectView homeRedirect(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return new RedirectView("/admin/home");
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            return new RedirectView("/teacher/home");
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            return new RedirectView("/student/home");
        }
        // fallback
        return new RedirectView("/login?error");
    }
}