package io.flexwork.modules.teams.service.dto;

import java.util.List;
import lombok.Data;

@Data
public class TransitionItemCollectionDTO {
    private Long ticketId;
    private List<TransitionItemDTO> transitions;

    public TransitionItemCollectionDTO(Long ticketId, List<TransitionItemDTO> transitions) {
        this.ticketId = ticketId;
        this.transitions = transitions;
    }
}
