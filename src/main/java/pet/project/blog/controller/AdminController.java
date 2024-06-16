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
import pet.project.blog.entity.User;
import pet.project.blog.service.UserService;

import java.util.List;

@Controller
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index/admin/{userId}")
    public String admin(@PathVariable String userId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        model.addAttribute("user", user);
        return "admin";
    }

    @GetMapping("/index/admin/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        model.addAttribute("user", user);

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
        return "redirect:/index/admin/users";
    }

    @PostMapping("/changeRole/{userId}")
    public String changeUserRole(@PathVariable String userId, RedirectAttributes redirectAttributes) {
        try {
            userService.changeUserRole(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User role is successfully changed");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/index/admin/users";
    }
}
