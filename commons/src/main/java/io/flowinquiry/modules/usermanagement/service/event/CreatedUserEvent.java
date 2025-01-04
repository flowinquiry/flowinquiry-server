package io.flowinquiry.modules.usermanagement.service.event;

import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreatedUserEvent extends ApplicationEvent {
    private UserDTO user;

    public CreatedUserEvent(Object source, UserDTO user) {
        super(source);
        this.user = user;
    }
}
