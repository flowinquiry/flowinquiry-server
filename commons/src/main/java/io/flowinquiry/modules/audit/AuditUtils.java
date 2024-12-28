package io.flowinquiry.modules.audit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AuditUtils {

    /**
     * Find the changes between two entities using the provided registry.
     *
     * @param oldEntity The existing (original) entity.
     * @param newEntity The updated entity.
     * @param registry The field handler registry for the entity type.
     * @return A list of FieldChange objects representing the differences.
     * @throws IllegalAccessException If a field is inaccessible via reflection.
     */
    public static List<FieldChange> findChanges(
            Object oldEntity, Object newEntity, EntityFieldHandlerRegistry registry)
            throws IllegalAccessException {

        // Validate that both entities are of the same type
        if (oldEntity == null
                || newEntity == null
                || !oldEntity.getClass().equals(newEntity.getClass())) {
            throw new IllegalArgumentException("Objects must be non-null and of the same type");
        }

        // Validate that the entity class matches the registry's entity class
        if (!oldEntity.getClass().equals(registry.getEntityClass())) {
            throw new IllegalArgumentException(
                    "Registry's entity class does not match the provided entity class");
        }

        List<FieldChange> changes = new ArrayList<>();
        Class<?> clazz = oldEntity.getClass();

        // Iterate over all declared fields of the entity
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // Make private fields accessible

            Object oldValue = field.get(oldEntity);
            Object newValue = field.get(newEntity);

            // Compare values and check for changes
            if ((oldValue == null && newValue != null)
                    || (oldValue != null && !oldValue.equals(newValue))) {
                EntityFieldHandler handler = registry.getHandler(field.getName());

                // Only add the fields to audit log if the handler is presented
                if (handler != null) {
                    oldValue = handler.getFieldGetter().apply(oldEntity, oldValue);
                    newValue = handler.getFieldGetter().apply(newEntity, newValue);
                    changes.add(new FieldChange(handler.getFieldName(), oldValue, newValue));
                }
            }
        }

        return changes;
    }

    /** Represents a single field change in the audit log. */
    public static class FieldChange {
        private final String fieldName;
        private final Object oldValue;
        private final Object newValue;

        public FieldChange(String fieldName, Object oldValue, Object newValue) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }
    }
}
