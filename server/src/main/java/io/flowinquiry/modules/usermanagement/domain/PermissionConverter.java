package io.flowinquiry.modules.usermanagement.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PermissionConverter implements AttributeConverter<Permission, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Permission permission) {
        return (permission != null) ? permission.getCode() : 0;
    }

    @Override
    public Permission convertToEntityAttribute(Integer code) {
        return (code != null) ? Permission.fromCode(code) : Permission.NONE;
    }
}
