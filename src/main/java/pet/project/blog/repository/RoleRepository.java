package pet.project.blog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.blog.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
