package pet.project.blog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.blog.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
