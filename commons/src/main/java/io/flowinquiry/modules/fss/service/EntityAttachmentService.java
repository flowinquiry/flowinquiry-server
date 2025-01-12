package io.flowinquiry.modules.fss.service;

import io.flowinquiry.modules.fss.repository.EntityAttachmentRepository;
import org.springframework.stereotype.Service;

@Service
public class EntityAttachmentService {
    private final EntityAttachmentRepository entityAttachmentRepository;

    private final StorageService storageService;

    public EntityAttachmentService(
            EntityAttachmentRepository entityAttachmentRepository, StorageService storageService) {
        this.entityAttachmentRepository = entityAttachmentRepository;
        this.storageService = storageService;
    }
}
