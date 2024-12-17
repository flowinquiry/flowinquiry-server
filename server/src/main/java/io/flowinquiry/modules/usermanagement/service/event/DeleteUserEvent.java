package io.flowinquiry.modules.usermanagement.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeleteUserEvent extends ApplicationEvent {
    private final Long userId;

    public DeleteUserEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
