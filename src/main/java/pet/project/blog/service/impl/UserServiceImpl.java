package pet.project.blog.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.Role;
import pet.project.blog.entity.User;
import pet.project.blog.repository.RoleRepository;
import pet.project.blog.repository.UserRepository;
import pet.project.blog.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

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
        // Check for null or empty strings
        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();

        user.setId(user.getId());
        user.setName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
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
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

/*
Метод удаления пользователей (пока ненужен)
    @Override
    public void deleteUserById(Long userId) {
        if ("null".equals(String.valueOf(userId))) {

            // Handle the case when userId is null
            throw new NullPointerException("User ID cannot be null");
//            throw new IllegalArgumentException("User ID cannot be 'null'");

        } else {
            userRepository.deleteById(userId);
        }
    }*/

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_USER");  // Corrected role name
        return roleRepository.save(role);
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setId(userDto.getId());
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return userDto;
    }
}