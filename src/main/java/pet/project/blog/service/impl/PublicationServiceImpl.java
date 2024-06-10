package pet.project.blog.service.impl;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.project.blog.entity.Publication;
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
        // Convert userId string to long
        Long id = Long.parseLong(userId);
        return publicationRepository.findByUserId(id);
    }


    @Override
    @Transactional
    public void editPublication(String pubId, String newTag, String newText) {
        Long id = parsePublicationId(pubId);

        Optional<Publication> optionalPublication = publicationRepository.findById(id);
        if (optionalPublication.isEmpty()) {
            throw new IllegalArgumentException("Publication with id " + pubId + " not found");
        }
        Publication publication = optionalPublication.get();

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
    public void deletePublication(String pubId) {
        Long id = parsePublicationId(pubId);

        Optional<Publication> optionalPublication = publicationRepository.findById(id);
        if (optionalPublication.isEmpty()) {
            throw new IllegalArgumentException("Publication with id " + pubId + " not found");
        }
        Publication publication = optionalPublication.get();
        publicationRepository.delete(publication);
    }

    // Helper method to parse publication ID from string to long (idk why i did this)
    private Long parsePublicationId(String pubId) {
        if (StringUtils.isBlank(pubId)) {
            throw new IllegalArgumentException("Publication ID cannot be empty");
        }
        try {
            return Long.parseLong(pubId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid publication ID format: " + pubId);
        }
    }
}
