package io.flowinquiry.modules.audit;

@FunctionalInterface
public interface FieldHandler {
    String handle(Object oldValue, Object newValue);
}
