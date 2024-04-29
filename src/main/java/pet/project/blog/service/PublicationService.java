package pet.project.blog.service;

import org.springframework.transaction.annotation.Transactional;
import pet.project.blog.entity.Publication;

import java.util.List;

public interface PublicationService {

    List<Publication> findPublicationsByUserId(String userId);

    @Transactional
    void editPublication(String pubId, String newTag, String newText);

    // TODO: Implement deletion of publications
    @Transactional
    void deletePublication(String pubId);
}
