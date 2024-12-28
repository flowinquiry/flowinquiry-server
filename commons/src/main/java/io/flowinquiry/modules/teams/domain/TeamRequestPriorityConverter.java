package io.flowinquiry.modules.teams.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TeamRequestPriorityConverter
        implements AttributeConverter<TeamRequestPriority, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TeamRequestPriority priority) {
        return (priority != null) ? priority.getCode() : null;
    }

    @Override
    public TeamRequestPriority convertToEntityAttribute(Integer code) {
        return (code != null) ? TeamRequestPriority.fromCode(code) : null;
    }
}
