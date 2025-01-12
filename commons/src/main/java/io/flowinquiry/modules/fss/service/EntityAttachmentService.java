package io.flowinquiry.modules.fss.service;

import io.flowinquiry.modules.fss.domain.EntityAttachment;
import io.flowinquiry.modules.fss.repository.EntityAttachmentRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EntityAttachmentService {
    private final EntityAttachmentRepository entityAttachmentRepository;

    private final StorageService storageService;

    public EntityAttachmentService(
            EntityAttachmentRepository entityAttachmentRepository, StorageService storageService) {
        this.entityAttachmentRepository = entityAttachmentRepository;
        this.storageService = storageService;
    }

    /**
     * Uploads a single attachment and returns an unsaved EntityAttachment object.
     *
     * @param entityType The type of entity (e.g., "team_request", "comment").
     * @param entityId The ID of the entity.
     * @param file The file to upload.
     * @return An unsaved EntityAttachment object.
     * @throws Exception If file storage fails or the file is empty.
     */
    private EntityAttachment createAttachment(String entityType, Long entityId, MultipartFile file)
            throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }

        // Store the file and get its URL
        String fileUrl =
                storageService.uploadFile(
                        StorageService.ATTACHMENTS,
                        file.getOriginalFilename(),
                        file.getInputStream());

        // Create the attachment entity
        EntityAttachment attachment = new EntityAttachment();
        attachment.setEntityType(entityType);
        attachment.setEntityId(entityId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFileUrl(fileUrl);
        attachment.setUploadedAt(LocalDateTime.now());

        return attachment;
    }

    /**
     * Uploads multiple attachments and associates them with a specific entity using batch insert.
     *
     * @param entityType The type of entity (e.g., "team_request", "comment").
     * @param entityId The ID of the entity.
     * @param files The list of files to upload.
     * @return A list of saved EntityAttachment objects.
     * @throws Exception If any file storage operation fails.
     */
    @Transactional
    public List<EntityAttachment> uploadAttachments(
            String entityType, Long entityId, MultipartFile[] files) throws Exception {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("File list cannot be empty.");
        }

        List<EntityAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            attachments.add(createAttachment(entityType, entityId, file));
        }

        // Perform batch insert for all attachments
        return entityAttachmentRepository.saveAll(attachments);
    }

    /**
     * Retrieves all attachments associated with a specific entity.
     *
     * @param entityType The type of entity (e.g., "team_request", "comment").
     * @param entityId The ID of the entity.
     * @return A list of attachments for the entity.
     */
    public List<EntityAttachment> getAttachments(String entityType, Long entityId) {
        return entityAttachmentRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    /**
     * Deletes all attachments associated with a specific entity.
     *
     * @param entityType The type of entity (e.g., "team_request", "comment").
     * @param entityId The ID of the entity.
     */
    public void deleteAttachments(String entityType, Long entityId) {
        entityAttachmentRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
    }
}
