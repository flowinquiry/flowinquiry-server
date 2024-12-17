package io.flowinquiry.modules.audit;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuditLogUpdateEvent extends ApplicationEvent {

    private final Object previousEntity;
    private final Object updatedEntity;

    public AuditLogUpdateEvent(Object source, Object previousEntity, Object updatedEntity) {
        super(source);
        this.previousEntity = previousEntity;
        this.updatedEntity = updatedEntity;
    }
}
