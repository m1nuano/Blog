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

    private final PublicationRepository publicationRepository;
    private final UserService userService;
    private final PublicationService publicationService;


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
        String creator = authentication.getName();
        User user = userService.findUserByEmail(creator);

        Publication publication = new Publication(text, tag, user);
        publicationRepository.save(publication);

        model.addAttribute("user", user);
        return ("redirect:/index");
    }

    @GetMapping("/index/publication_edit/{pubId}")
    public String showEditForm(Model model, @PathVariable String pubId) {
        // Получаем текущую публикацию
        Publication publication = publicationService.getPublicationById(pubId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);

        // current user ? creator
        if (publication.getUser() == null || !publication.getUser().equals(currentUser)) {
            return "redirect:/index?error=Unauthorized";
        }
        // Adding current publication data to the model
        model.addAttribute("publication", publication);
        return "publication_edit";
    }

    @PostMapping("/index/publication_edit/{pubId}")
    public String editPublication(@PathVariable String pubId,
                                  @RequestParam String newText,
                                  @RequestParam String newTag) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);
        // Current character limit
        if (newText.length() > 255) {
            return "redirect:/index/publication_edit/" + pubId + "?error=TextTooLong";
        }

        if (StringUtils.isBlank(newText) && StringUtils.isBlank(newTag)) {
            return "redirect:/index?error=NoChanges";
        }

        try {
            publicationService.editPublication(pubId, newTag, newText, currentUser);
        } catch (IllegalArgumentException e) {
            return "redirect:/index?error=" + e.getMessage();
        }
        return "redirect:/index";
    }

    @PostMapping("/deletePublication/{pubId}")
    public String deletePublication(@PathVariable String pubId) {
        Publication publication = publicationService.getPublicationById(pubId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);

        if (publication.getUser() == null || !publication.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        publicationService.deletePublication(pubId, currentUser);
        return "redirect:/index";
    }
}
