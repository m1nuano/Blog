package pet.project.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.blog.entity.Publication;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByUserId(Long userId);
}
