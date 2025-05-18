package io.flowinquiry.modules.teams.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TicketPriorityConverter implements AttributeConverter<TicketPriority, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TicketPriority priority) {
        return (priority != null) ? priority.getCode() : null;
    }

    @Override
    public TicketPriority convertToEntityAttribute(Integer code) {
        return (code != null) ? TicketPriority.fromCode(code) : null;
    }
}
