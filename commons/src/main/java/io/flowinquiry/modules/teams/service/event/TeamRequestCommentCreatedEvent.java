package io.flowinquiry.modules.teams.service.event;

import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TeamRequestCommentCreatedEvent extends ApplicationEvent {
    private final CommentDTO commentDTO;

    public TeamRequestCommentCreatedEvent(Object source, CommentDTO commentDTO) {
        super(source);
        this.commentDTO = commentDTO;
    }
}
