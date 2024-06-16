package pet.project.blog.service;

import org.springframework.transaction.annotation.Transactional;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.User;

import java.util.List;

public interface PublicationService {

    List<Publication> findPublicationsByUserId(String userId);

    @Transactional
    void editPublication(String pubId, String newTag, String newText, User currentUser);

    @Transactional
    void deletePublication(String pubId, User user);

    Iterable<Publication> findAll();

    Publication getPublicationById(String pubId);
}
