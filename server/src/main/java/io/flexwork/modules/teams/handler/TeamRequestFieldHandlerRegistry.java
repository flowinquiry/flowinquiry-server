package io.flexwork.modules.teams.handler;

import io.flexwork.modules.audit.AbstractEntityFieldHandlerRegistry;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class TeamRequestFieldHandlerRegistry extends AbstractEntityFieldHandlerRegistry {
    @Override
    protected void initializeFieldHandlers() {}

    @Override
    public Class<?> getEntityClass() {
        return TeamRequestDTO.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Team_Request;
    }
}
