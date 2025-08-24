package io.jos.onlinelearningplatform.controller.publicsite;

import io.jos.onlinelearningplatform.facade.PublicTeacherFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
public class PublicTeacherController {

    private final PublicTeacherFacade publicTeacherFacade;

    public PublicTeacherController(PublicTeacherFacade publicTeacherFacade) {
        this.publicTeacherFacade = publicTeacherFacade;
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        return publicTeacherFacade.prepareTeacherProfile(id, model);
    }
}
