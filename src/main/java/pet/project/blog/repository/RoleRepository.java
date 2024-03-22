package pet.project.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.blog.entity.Role;
import pet.project.blog.enums.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(RoleEnum role);
}
