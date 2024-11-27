package io.flexwork.modules.audit;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuditLogUpdateEvent extends ApplicationEvent {

    private final Object updatedEntity;

    public AuditLogUpdateEvent(Object source, Object updatedEntity) {
        super(source);
        this.updatedEntity = updatedEntity;
    }
}
