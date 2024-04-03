package pet.project.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.project.blog.entity.Publication;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

}
