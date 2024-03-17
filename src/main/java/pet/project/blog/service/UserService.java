package pet.project.blog.service;


import pet.project.blog.dto.UserDto;
import pet.project.blog.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findUserByEmail(String email);
    List<UserDto> findAllUsers();


    //Метод удаления пользователей
    void deleteUserById(String userId);
}