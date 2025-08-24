package io.jos.onlinelearningplatform.controller;

import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView showRegisterForm(Model model) {
        model.addAttribute("dto", new RegisterDto());
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(@ModelAttribute RegisterDto dto, Model model) {
        try {
            userService.register(dto);
            return new ModelAndView("redirect:/login?registered=true");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dto", dto);
            return new ModelAndView("register");
        }
    }

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");   // resolves to src/main/resources/templates/login.html
    }
}
