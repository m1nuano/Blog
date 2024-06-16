package pet.project.blog.service.impl;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.project.blog.entity.Publication;
import pet.project.blog.entity.User;
import pet.project.blog.repository.PublicationRepository;
import pet.project.blog.service.PublicationService;

import java.util.List;
import java.util.Optional;

@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;

    public PublicationServiceImpl(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    @Override
    public List<Publication> findPublicationsByUserId(String userId) {
        Long id = idParser(userId);
        return publicationRepository.findByUserId(id);
    }

    @Override
    @Transactional
    public void editPublication(String pubId, String newTag, String newText, User currentUser) {
        Publication publication = getPublicationWithCurrentUser(pubId);
        // Checking for the number of characters
        if (newText.length() > 255) {
            throw new IllegalArgumentException("TextTooLong");
        }
        // Update publication
        if (StringUtils.isNotBlank(newTag)) {
            publication.setTag(newTag);
        }
        if (StringUtils.isNotBlank(newText)) {
            publication.setText(newText);
        }
        publicationRepository.save(publication);
    }

    @Override
    @Transactional
    public void deletePublication(String pubId, User currentUser) {
        publicationRepository.delete(getPublicationWithCurrentUser(pubId));
    }

    @Override
    public Iterable<Publication> findAll() {
        return publicationRepository.findAll();
    }

    @Override
    public Publication getPublicationById(String pubId) {
        return publicationRepository.findById(getPublicationWithCurrentUser(pubId).getId()).orElse(null);
    }

    private Long idParser(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format: " + id);
        }
    }

    private Publication getPublicationWithCurrentUser(String pubId) {
        Long id = idParser(pubId);
        Optional<Publication> optionalPublication = publicationRepository.findById(id);
        if (optionalPublication.isEmpty()) {
            throw new IllegalArgumentException("Publication with id " + pubId + " not found");
        }
        return optionalPublication.get();
    }
}
