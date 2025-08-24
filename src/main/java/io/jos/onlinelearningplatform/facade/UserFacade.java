package io.jos.onlinelearningplatform.facade;

import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

@Component
public class UserFacade {

    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public ModelAndView prepareRegisterForm(Model model) {
        model.addAttribute("dto", new RegisterDto());
        return new ModelAndView("register");
    }

    public ModelAndView handleRegistration(RegisterDto dto, Model model) {
        try {
            userService.register(dto);
            return new ModelAndView("redirect:/login?registered=true");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dto", dto);
            return new ModelAndView("register");
        }
    }

    public ModelAndView showLoginPage() {
        return new ModelAndView("login");
    }
}
