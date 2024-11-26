package io.flexwork.modules.teams.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TicketChannelConverter implements AttributeConverter<TicketChannel, String> {

    @Override
    public String convertToDatabaseColumn(TicketChannel channel) {
        return channel != null ? channel.getDisplayName() : null;
    }

    @Override
    public TicketChannel convertToEntityAttribute(String dbData) {
        return dbData != null ? TicketChannel.fromDisplayName(dbData) : null;
    }
}
