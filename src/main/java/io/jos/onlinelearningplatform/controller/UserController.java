package io.jos.onlinelearningplatform.controller;

import io.jos.onlinelearningplatform.dto.RegisterDto;
import io.jos.onlinelearningplatform.facade.UserFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/register")
    public ModelAndView showRegisterForm(Model model) {
        return userFacade.prepareRegisterForm(model);
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(@ModelAttribute RegisterDto dto, Model model) {
        return userFacade.handleRegistration(dto, model);
    }

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return userFacade.showLoginPage();
    }
}
