package pet.project.blog.controller;


import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.User;
import pet.project.blog.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    private UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    // Now we pass the user even to index
    @GetMapping("/index")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        if (user != null) {
            model.addAttribute("user", user);
            return "index"; // Redirect to profile URL
        } else {
            return "error"; // Handle the case when the user is not found
        }
    }


    @GetMapping("/profile/{userId}")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            return "error";
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
