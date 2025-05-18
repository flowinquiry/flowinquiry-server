package io.flowinquiry.modules.teams.handler;

import io.flowinquiry.modules.audit.AbstractEntityFieldHandlerRegistry;
import io.flowinquiry.modules.audit.EntityFieldHandler;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.teams.domain.TicketChannel;
import io.flowinquiry.modules.teams.repository.WorkflowStateRepository;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TicketFieldHandlerRegistry extends AbstractEntityFieldHandlerRegistry {

    private final UserRepository userRepository;

    private final WorkflowStateRepository workflowStateRepository;

    public TicketFieldHandlerRegistry(
            UserRepository userRepository, WorkflowStateRepository workflowStateRepository) {
        this.userRepository = userRepository;
        this.workflowStateRepository = workflowStateRepository;
    }

    @Override
    protected void initializeFieldHandlers() {
        addFieldHandler("requestTitle", new EntityFieldHandler<TicketDTO>("Title"));
        addFieldHandler("requestDescription", new EntityFieldHandler<TicketDTO>("Description"));
        addFieldHandler("priority", new EntityFieldHandler<TicketDTO>("Priority"));
        addFieldHandler(
                "channel",
                new EntityFieldHandler<TicketDTO>(
                        "Channel",
                        (objectVal, channel) ->
                                Optional.ofNullable((TicketChannel) channel)
                                        .map(TicketChannel::getDisplayName)
                                        .orElse("")));
        addFieldHandler(
                "estimatedCompletionDate",
                new EntityFieldHandler<TicketDTO>("Target Completion Date"));
        addFieldHandler(
                "actualCompletionDate",
                new EntityFieldHandler<TicketDTO>("Actual Completion Date"));
        addFieldHandler(
                "currentStateId",
                new EntityFieldHandler<TicketDTO>(
                        "State",
                        (objectVal, fieldVal) ->
                                Optional.ofNullable(fieldVal)
                                        .flatMap(
                                                id ->
                                                        workflowStateRepository
                                                                .findById((Long) id)
                                                                .map(state -> state.getStateName()))
                                        .orElse("")));
        addFieldHandler(
                "assignUserId",
                new EntityFieldHandler<>(
                        "Assigned User",
                        (objectVal, fieldVal) ->
                                Optional.ofNullable(fieldVal)
                                        .flatMap(id -> userRepository.findById((Long) id))
                                        .map(user -> user.getFirstName() + " " + user.getLastName())
                                        .orElse("")));
    }

    @Override
    public Class<?> getEntityClass() {
        return TicketDTO.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Ticket;
    }
}
