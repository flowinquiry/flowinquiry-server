package io.flowinquiry.modules.teams.handler;

import io.flowinquiry.modules.audit.service.AbstractEntityFieldHandlerRegistry;
import io.flowinquiry.modules.audit.service.EntityFieldHandler;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.teams.domain.TicketChannel;
import io.flowinquiry.modules.teams.repository.WorkflowStateRepository;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Registry for ticket field handlers. This class is responsible for mapping ticket field names to
 * their display names and providing formatters for displaying field values in a user-friendly way.
 * It is used for audit logging, displaying field changes, and other scenarios where ticket field
 * values need to be formatted for display.
 */
@Component
public class TicketFieldHandlerRegistry extends AbstractEntityFieldHandlerRegistry {

    private final UserRepository userRepository;

    private final WorkflowStateRepository workflowStateRepository;

    /**
     * Constructs a new TicketFieldHandlerRegistry with the required repositories.
     *
     * @param userRepository Repository for looking up user information
     * @param workflowStateRepository Repository for looking up workflow state information
     */
    public TicketFieldHandlerRegistry(
            UserRepository userRepository, WorkflowStateRepository workflowStateRepository) {
        this.userRepository = userRepository;
        this.workflowStateRepository = workflowStateRepository;
    }

    /**
     * Initializes all field handlers for ticket entities. This method registers handlers for
     * various ticket fields, mapping them to their display names and providing custom formatters
     * for complex fields like channel, state, and assigned user.
     */
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

    /**
     * Returns the class of the entity this registry handles.
     *
     * @return The TicketDTO class
     */
    @Override
    public Class<?> getEntityClass() {
        return TicketDTO.class;
    }

    /**
     * Returns the entity type this registry handles.
     *
     * @return The Ticket entity type
     */
    @Override
    public EntityType getEntityType() {
        return EntityType.Ticket;
    }
}
