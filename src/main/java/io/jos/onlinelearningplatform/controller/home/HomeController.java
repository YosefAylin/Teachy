package io.jos.onlinelearningplatform.controller.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeController {

    @GetMapping("/" )
    public ModelAndView home() {
        return new ModelAndView("home"); // This will resolve to src/main/resources/templates/home.html
    }
}