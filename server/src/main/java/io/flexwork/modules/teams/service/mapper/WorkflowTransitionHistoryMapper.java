package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.WorkflowTransitionHistory;
import io.flexwork.modules.teams.service.dto.TransitionItemCollectionDTO;
import io.flexwork.modules.teams.service.dto.TransitionItemDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkflowTransitionHistoryMapper {
    @Mapping(source = "fromState.stateName", target = "fromState")
    @Mapping(source = "toState.stateName", target = "toState")
    TransitionItemDTO toTransitionItemDto(WorkflowTransitionHistory history);

    default TransitionItemCollectionDTO toTicketHistoryDto(
            Long ticketId, List<WorkflowTransitionHistory> histories) {
        List<TransitionItemDTO> transitions =
                histories.stream().map(this::toTransitionItemDto).toList();
        return new TransitionItemCollectionDTO(ticketId, transitions);
    }
}
