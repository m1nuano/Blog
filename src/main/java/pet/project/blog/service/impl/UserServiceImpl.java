package pet.project.blog.service.impl;

import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.Role;
import pet.project.blog.entity.User;
import pet.project.blog.enums.RoleEnum;
import pet.project.blog.repository.RoleRepository;
import pet.project.blog.repository.UserRepository;
import pet.project.blog.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        Long id = userDto.getId();
        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();

        user.setId(id);
        user.setName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByRole(RoleEnum.ROLE_USER);
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changeUserRole(String userId) {
        // Check for a null or empty user ID
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть пустым");
        }

        try {
            Long id = Long.parseLong(userId);

            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                throw new IllegalArgumentException("Пользователь с указанным идентификатором не найден");
            }
            User user = optionalUser.get();

            // Define a new role
            Role newRole = user.getRoles().stream()
                    .findFirst()
                    .map(role -> {
                        if (role.getRole() == RoleEnum.ROLE_USER) {
                            return roleRepository.findByRole(RoleEnum.ROLE_ADMIN);
                        } else if (role.getRole() == RoleEnum.ROLE_ADMIN) {
                            return roleRepository.findByRole(RoleEnum.ROLE_USER);
                        } else {
                            return null; // Return null if the user does not have any of the roles (unexpected situation)
                        }
                    })
                    .orElseThrow(() -> new IllegalStateException("У пользователя отсутствует какая-либо роль"));

            // Check if the user has the ROLE_ADMIN role
            boolean hasAdminRole = user.getRoles().stream()
                    .anyMatch(role -> role.getRole() == RoleEnum.ROLE_ADMIN);

            // If the user does not have the ROLE_ADMIN role, add it
            if (!hasAdminRole) {
                Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN);
                user.getRoles().add(adminRole);
            }

            // Replace the user role with a new one
            user.getRoles().removeIf(role -> role.getRole() == RoleEnum.ROLE_USER || role.getRole() == RoleEnum.ROLE_ADMIN);
            user.getRoles().add(newRole);

            userRepository.save(user);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат идентификатора пользователя. Пожалуйста, укажите допустимый числовой идентификатор.");
        }
    }


    private Role checkRoleExist() {
        Role role = new Role();
        role.setRole(RoleEnum.ROLE_USER);
        return roleRepository.save(role);
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setId(user.getId());
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles().stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.toList()));
        return userDto;
    }

    @Override
    public void deleteUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        } else {
            try {
                Long id = Long.parseLong(userId);
                User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
                user.getRoles().clear();
                userRepository.save(user);
                userRepository.deleteById(id);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getName().equals(user.getName())) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid User ID format. Please provide a valid numeric ID.");
            }
        }
    }
}
