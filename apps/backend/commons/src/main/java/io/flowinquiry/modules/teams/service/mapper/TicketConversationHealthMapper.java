package io.flowinquiry.modules.teams.service.mapper;

import io.flowinquiry.modules.teams.domain.TicketConversationHealth;
import io.flowinquiry.modules.teams.service.dto.TicketConversationHealthDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketConversationHealthMapper {
    /**
     * Converts a TicketConversationHealth entity to its DTO.
     *
     * @param entity the TicketConversationHealth entity
     * @return the TicketConversationHealthDTO
     */
    @Mapping(source = "ticket.id", target = "ticketId")
    TicketConversationHealthDTO toDTO(TicketConversationHealth entity);

    /**
     * Converts a TicketConversationHealthDTO to its entity.
     *
     * @param dto the TicketConversationHealthDTO
     * @return the TicketConversationHealth entity
     */
    @Mapping(source = "ticketId", target = "ticket.id")
    TicketConversationHealth toEntity(TicketConversationHealthDTO dto);
}
