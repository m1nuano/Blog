package pet.project.blog.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.Role;
import pet.project.blog.entity.User;
import pet.project.blog.enums.RoleEnum;
import pet.project.blog.repository.PublicationRepository;
import pet.project.blog.repository.RoleRepository;
import pet.project.blog.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_ShouldSaveUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("Test1");
        userDto.setLastName("Test2");
        userDto.setUsername("TestCase");
        userDto.setEmail("test.testovich@example.com");
        userDto.setPassword("test-password");

        Role role = new Role();
        role.setRole(RoleEnum.ROLE_ADMIN);

        when(roleRepository.findByRole(any(RoleEnum.class))).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.saveUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findUserByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test.testovich@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        User result = userService.findUserByEmail("test.testovich@example.com");

        assertEquals("test.testovich@example.com", result.getEmail());
    }

    @Test
    void changeUserRole_ShouldChangeUserRole() {
        User user = new User();
        user.setId(1L);
        Role roleUser = new Role();
        roleUser.setRole(RoleEnum.ROLE_USER);
        user.setRoles(Collections.singletonList(roleUser));

        Role roleAdmin = new Role();
        roleAdmin.setRole(RoleEnum.ROLE_ADMIN);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleRepository.findAll()).thenReturn(Collections.singletonList(roleAdmin));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.changeUserRole("1");

        assertEquals(RoleEnum.ROLE_ADMIN, user.getRoles().get(0).getRole());
    }

    @Test
    void deleteUserById_ShouldDeleteUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("TestCase");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deleteUserById("1");

        verify(userRepository, times(1)).deleteById(1L);
    }
}
