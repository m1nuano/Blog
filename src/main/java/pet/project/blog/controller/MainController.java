package pet.project.blog.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.User;
import pet.project.blog.repository.PublicationRepository;
import pet.project.blog.service.PublicationService;
import pet.project.blog.service.UserService;

@Controller
public class MainController {

    private final UserService userService;
    private final PublicationService publicationService;

    public MainController(UserService userService, PublicationService publicationService) {
        this.userService = userService;
        this.publicationService = publicationService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        //To view publications
        Iterable<Publication> publications = publicationService.findAll();
        model.addAttribute("public", publications);
        //
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        model.addAttribute("user", user);
        return "index";
    }
}
