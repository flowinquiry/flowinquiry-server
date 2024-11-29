package io.flexwork.modules.teams.handler;

import io.flexwork.modules.audit.AbstractEntityFieldHandlerRegistry;
import io.flexwork.modules.audit.EntityFieldHandler;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.teams.domain.TicketChannel;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TeamRequestFieldHandlerRegistry extends AbstractEntityFieldHandlerRegistry {

    private final UserRepository userRepository;

    public TeamRequestFieldHandlerRegistry(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void initializeFieldHandlers() {
        addFieldHandler("priority", new EntityFieldHandler<TeamRequestDTO>("Priority"));
        addFieldHandler(
                "channel",
                new EntityFieldHandler<TeamRequestDTO>(
                        "Channel",
                        (objectVal, channel) ->
                                Optional.ofNullable((TicketChannel) channel)
                                        .map(TicketChannel::getDisplayName)
                                        .orElse("")));
        addFieldHandler(
                "estimatedCompletionDate",
                new EntityFieldHandler<TeamRequestDTO>("Target Completion Date"));
        addFieldHandler(
                "actualCompletionDate",
                new EntityFieldHandler<TeamRequestDTO>("Actual Completion Date"));
        addFieldHandler("currentState", new EntityFieldHandler<TeamRequestDTO>("Current State"));
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
        return TeamRequestDTO.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Team_Request;
    }
}
