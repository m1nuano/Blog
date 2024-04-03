package pet.project.blog.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.User;
import pet.project.blog.repository.PublicationRepository;
import pet.project.blog.service.UserService;

import java.security.Principal;

@Controller
public class PublicationController {

    private PublicationRepository publicationRepository;
    private UserService userService;


    public PublicationController(UserService userService, PublicationRepository publicationRepository) {
        this.userService = userService;
        this.publicationRepository = publicationRepository;
    }

    @GetMapping("/publication")
    public String add(Model model) {
        return "publication";
    }

    @PostMapping("/publication")
    public String savePublication(Model model, @RequestParam String text, @RequestParam String tag, Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);

        Publication publication = new Publication(text, tag, user);
        publicationRepository.save(publication);
        // Pass the user to the index page
        model.addAttribute("user", user);
        return ("redirect:/index");
    }
}
