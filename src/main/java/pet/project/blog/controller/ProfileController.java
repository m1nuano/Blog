package pet.project.blog.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.User;
import pet.project.blog.service.PublicationService;
import pet.project.blog.service.UserService;

import java.util.List;

@Controller
public class ProfileController {

    private final UserService userService;
    private final PublicationService publicationService;

    public ProfileController(UserService userService, PublicationService publicationService) {
        this.userService = userService;
        this.publicationService = publicationService;
    }

    @GetMapping("/index/profile/{userId}")
    public String userProfileOrOtherProfile(@PathVariable String userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);

        // Here we check whether the user is visiting his own profile or someone else’s
        if (currentUser != null) {
            if (currentUser.getId().equals(Long.valueOf(userId))) {
                List<Publication> publications = publicationService.findPublicationsByUserId(userId);
                model.addAttribute("user", currentUser);
                model.addAttribute("public", publications);
                return "profile";
            } else {
                UserDto userDto = userService.findUserById(userId);
                List<Publication> publications = publicationService.findPublicationsByUserId(userId);
                if (userDto != null) {
                    model.addAttribute("user", userDto);
                    model.addAttribute("public", publications);
                    return "other_profile";
                } else {
                    return "user_not_found";
                }
            }
        } else {
            // Handle the situation when the user is not found
            return "user_not_found";
        }
    }
}
