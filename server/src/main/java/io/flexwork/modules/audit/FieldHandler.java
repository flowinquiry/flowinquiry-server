package io.flexwork.modules.audit;

@FunctionalInterface
public interface FieldHandler {
    String handle(Object oldValue, Object newValue);
}
