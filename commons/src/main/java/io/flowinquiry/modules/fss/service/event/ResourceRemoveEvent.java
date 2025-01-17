package io.flowinquiry.modules.fss.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ResourceRemoveEvent extends ApplicationEvent {
    private final String objectPath;

    public ResourceRemoveEvent(Object source, String objectPath) {
        super(source);
        this.objectPath = objectPath;
    }
}
