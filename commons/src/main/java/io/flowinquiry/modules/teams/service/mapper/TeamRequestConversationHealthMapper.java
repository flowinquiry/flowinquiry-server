package io.flowinquiry.modules.teams.service.mapper;

import io.flowinquiry.modules.teams.domain.TeamRequestConversationHealth;
import io.flowinquiry.modules.teams.service.dto.TeamRequestConversationHealthDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamRequestConversationHealthMapper {
    /**
     * Converts a TeamRequestConversationHealth entity to its DTO.
     *
     * @param entity the TeamRequestConversationHealth entity
     * @return the TeamRequestConversationHealthDTO
     */
    @Mapping(source = "teamRequest.id", target = "teamRequestId")
    TeamRequestConversationHealthDTO toDTO(TeamRequestConversationHealth entity);

    /**
     * Converts a TeamRequestConversationHealthDTO to its entity.
     *
     * @param dto the TeamRequestConversationHealthDTO
     * @return the TeamRequestConversationHealth entity
     */
    @Mapping(source = "teamRequestId", target = "teamRequest.id")
    TeamRequestConversationHealth toEntity(TeamRequestConversationHealthDTO dto);
}
