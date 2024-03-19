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
        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();

        user.setId(id);
        user.setName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        //Измените здесь на "ROLE_ADMIN" чтобы зарегистрировать пользователя с правами администратора
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


    //Метод удаления пользователей
    @Override
    public void deleteUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        } else {
            try {
                Long id = Long.parseLong(userId);
                // Поиск пользователя по ID
                User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
                // Удаление всех ролей
                user.getRoles().clear();
                // Сохраняем изменения
                userRepository.save(user);
                // Удаление пользователя
                userRepository.deleteById(id);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid User ID format. Please provide a valid numeric ID.");
            }
        }
    }


    private Role checkRoleExist() {
        Role role = new Role();
        //Измените здесь на "ROLE_ADMIN" чтобы зарегистрировать пользователя с правами администратора
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setId(user.getId());
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return userDto;
    }
}