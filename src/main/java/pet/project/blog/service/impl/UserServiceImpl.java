package pet.project.blog.service.impl;

import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.Role;
import pet.project.blog.entity.User;
import pet.project.blog.enums.RoleEnum;
import pet.project.blog.repository.PublicationRepository;
import pet.project.blog.repository.RoleRepository;
import pet.project.blog.repository.UserRepository;
import pet.project.blog.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PublicationRepository publicationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PublicationRepository publicationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.publicationRepository = publicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Serves to change the role of only the registered user, set ROLE_USER if you want new users to have the USER role by default
    RoleEnum RoleSwitcher = RoleEnum.ROLE_USER;

    // Updated saveUser method in UserServiceImpl
    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        Long id = userDto.getId();
        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();
        String nickName = userDto.getUsername();

        user.setId(id);
        user.setFirstName(firstName != null ? firstName : "");
        user.setLastName(lastName != null ? lastName : "");
        user.setUsername(nickName != null ? nickName : (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByRole(RoleSwitcher);
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
    public UserDto findUserById(String userId) {
        Long id = Long.parseLong(userId);
        try {
            Optional<User> userOptional = userRepository.findById(id);
            return userOptional
                    .map(this::mapToUserDto)
                    .orElse(null);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid User ID format. Please provide a valid numeric ID.");
        }
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
            // Algorithm for changing the cyclic role change
            // Check that the user has roles
            if (user.getRoles().isEmpty()) {
                throw new IllegalStateException("У пользователя отсутствует какая-либо роль");
            }

            // Get all roles from the database
            List<Role> allRoles = roleRepository.findAll();

            // Determine the current user role
            Role currentRole = user.getRoles().get(0); // Assume that the user always has only one role

            // Determine the index of the current role in the list of all roles
            int currentIndex = allRoles.indexOf(currentRole);

            // Calculate the index of the next role
            int nextIndex = (currentIndex + 1) % allRoles.size();

            // Get the next role
            Role newRole = allRoles.get(nextIndex);

            // Set a new role for the user
            user.getRoles().clear();
            user.getRoles().add(newRole);

            userRepository.save(user);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат идентификатора пользователя. Пожалуйста, укажите допустимый числовой идентификатор.");
        }
    }


    private Role checkRoleExist() {
        Role role = new Role();
        //
        role.setRole(RoleSwitcher);
        return roleRepository.save(role);
    }

    // Updated mapToUserDto method in UserServiceImpl
    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
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
                if (auth != null && auth.getName().equals(user.getUsername())) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid User ID format. Please provide a valid numeric ID.");
            }
        }
    }

    @Override
    public UserDto findUserByPublication(String ID) {
        Long id = Long.parseLong(ID);
        Optional<Publication> publication = publicationRepository.findById(id);
        if (publication.isPresent()) {
            Publication pub = publication.get();
            if (pub.getUser() != null) {
                User user = pub.getUser();
                return mapToUserDto(user);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
