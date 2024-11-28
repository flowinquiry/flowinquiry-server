package io.flexwork.modules.audit;

import java.util.function.BiFunction;
import lombok.Getter;

@Getter
public class EntityFieldHandler<T> {

    private String fieldName;
    private BiFunction<T, Object, String> fieldGetter;

    public EntityFieldHandler(String fieldName) {
        this(fieldName, ((objectVal, fieldVal) -> fieldVal == null ? "" : fieldVal.toString()));
    }

    public EntityFieldHandler(String displayField, BiFunction<T, Object, String> fieldGetter) {
        this.fieldName = displayField;
        this.fieldGetter = fieldGetter;
    }
}
