package io.flowinquiry.modules.fss.service;

import io.flowinquiry.modules.fss.service.event.ResourceRemoveEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ResourceRemoveListener {
    private final StorageService storageService;

    public ResourceRemoveListener(StorageService storageService) {
        this.storageService = storageService;
    }

    @Async
    @EventListener
    public void removeResource(ResourceRemoveEvent event) throws Exception {
        String removedObjectPath = event.getObjectPath();
        storageService.deleteFile(removedObjectPath);
    }
}
