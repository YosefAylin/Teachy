package io.jos.onlinelearningplatform.controller;

import io.jos.onlinelearningplatform.facade.MainFacade;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final MainFacade mainFacade;

    public MainController(MainFacade mainFacade) {
        this.mainFacade = mainFacade;
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return mainFacade.getHomePage();
    }

    @GetMapping("/home/redirect")
    public String redirectBasedOnRole(Authentication auth) {
        return mainFacade.redirectByRole(auth);
    }
}
