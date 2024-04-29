package pet.project.blog.controller;


import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.User;
import pet.project.blog.repository.PublicationRepository;
import pet.project.blog.service.PublicationService;
import pet.project.blog.service.UserService;

import java.security.Principal;

@Controller
public class PublicationController {

    private PublicationRepository publicationRepository;
    private UserService userService;
    private PublicationService publicationService;


    public PublicationController(UserService userService, PublicationRepository publicationRepository, PublicationService publicationService) {
        this.userService = userService;
        this.publicationRepository = publicationRepository;
        this.publicationService = publicationService;
    }

    @GetMapping("/index/publication")
    public String add(Model model) {
        return "publication";
    }

    @PostMapping("/index/publication")
    public String savePublication(Model model, @RequestParam String text, @RequestParam String tag, Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);

        Publication publication = new Publication(text, tag, user);
        publicationRepository.save(publication);

        model.addAttribute("user", user);
        return ("redirect:/index");
    }


    @GetMapping("/index/publication_edit/{pubId}")
    public String showEditForm(Model model, @PathVariable String pubId) {
        // Add the publication ID to the model
        model.addAttribute("pubId", pubId);
        return "publication_edit";
    }


    @PostMapping("/index/publication_edit/{pubId}")
    public String editPublication(Model model, @PathVariable String pubId, String newText, String newTag) {
        // Checking for changes in publication
        if (StringUtils.isBlank(newText) && StringUtils.isBlank(newTag)) {
            return "redirect:/index?error=NoChanges";
        }

        try {
            publicationService.editPublication(pubId, newTag, newText);
        } catch (IllegalArgumentException e) {
            return "redirect:/index?error=" + e.getMessage();
        }
        return "redirect:/index";
    }

}
