package io.flexwork.modules.fss;

import org.springframework.context.ApplicationEvent;

public class ResourceRemoveEvent extends ApplicationEvent {
    private String objectPath;

    public ResourceRemoveEvent(Object source, String objectPath) {
        super(source);
        this.objectPath = objectPath;
    }

    public String getObjectPath() {
        return objectPath;
    }
}
