package io.flowinquiry.modules.audit.service;

@FunctionalInterface
public interface FieldHandler {
    String handle(Object oldValue, Object newValue);
}
