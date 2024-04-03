package pet.project.blog.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.User;
import pet.project.blog.repository.PublicationRepository;
import pet.project.blog.service.UserService;

import java.util.List;

@Controller
public class MainController {

    private UserService userService;
    private PublicationRepository publicationRepository;

    public MainController(UserService userService, PublicationRepository publicationRepository) {
        this.userService = userService;
        this.publicationRepository = publicationRepository;
    }

    // Now we pass the user even to index
    @GetMapping("/index")
    public String index(Model model) {
        //To view publications
        Iterable<Publication> publications = publicationRepository.findAll();
        model.addAttribute("public", publications);
        //
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        model.addAttribute("user", user);
        return "index"; // Redirect to profile URL
    }

    @GetMapping("/profile/{userId}")
    public String userProfileOrOtherProfile(@PathVariable String userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findUserByEmail(email);

        if (currentUser != null) {
            // Check if the IDs of the current user and the requested user are equal
            if (currentUser.getId().equals(Long.valueOf(userId))) {
                // If the current user is viewing their own profile
                model.addAttribute("user", currentUser);
                return "profile";
            } else {
                // If the current user is viewing another user's profile
                UserDto userDto = userService.findUserById(userId);
                if (userDto != null) {
                    model.addAttribute("user", userDto);
                    return "other_profile";
                } else {
                    // Handle the situation when the user is not found
                    return "user_not_found";
                }
            }
        } else {
            // Handle the situation when the current user is not found
            return "user_not_found";
        }
    }


    @GetMapping("/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable String userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User successfully deleted");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("changeRole/{userId}")
    public String changeUserRole(@PathVariable String userId, RedirectAttributes redirectAttributes) {
        try {
            userService.changeUserRole(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User role is successfully changed");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}
