package io.flowinquiry.modules.fss.controller;

import io.flowinquiry.modules.fss.domain.EntityAttachment;
import io.flowinquiry.modules.fss.service.EntityAttachmentService;
import io.flowinquiry.modules.fss.service.dto.EntityAttachmentDTO;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/entity-attachments")
public class EntityAttachmentController {

    private final EntityAttachmentService attachmentService;

    public EntityAttachmentController(EntityAttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    /**
     * Upload multiple attachments and associate them with a specific entity.
     *
     * @param entityType The type of entity (e.g., "ticket", "comment").
     * @param entityId The ID of the entity.
     * @param files The list of files to upload.
     * @return A list of saved attachment entities.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<EntityAttachment>> uploadAttachments(
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId,
            @RequestPart("files") MultipartFile[] files)
            throws Exception {
        List<EntityAttachment> attachments =
                attachmentService.uploadAttachments(entityType, entityId, files);
        return ResponseEntity.ok(attachments);
    }

    /**
     * Retrieve all attachments for a specific entity.
     *
     * @param entityType The type of entity (e.g., "ticket", "comment").
     * @param entityId The ID of the entity.
     * @return A list of attachments for the specified entity.
     */
    @GetMapping
    public ResponseEntity<List<EntityAttachmentDTO>> getAttachments(
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId) {
        List<EntityAttachmentDTO> attachments =
                attachmentService.getAttachments(entityType, entityId);
        return ResponseEntity.ok(attachments);
    }

    /**
     * Deletes an attachment by its ID.
     *
     * @param attachmentId The ID of the attachment to delete.
     * @return A ResponseEntity indicating the result of the deletion.
     */
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable("attachmentId") Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
