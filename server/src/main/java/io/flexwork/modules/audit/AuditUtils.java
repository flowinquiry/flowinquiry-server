package io.flexwork.modules.audit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

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
                BiFunction<Object, Object, String> handler = registry.getHandler(field.getName());
                String description;

                // Use custom handler if available, otherwise provide a default change description
                if (handler != null) {
                    description = handler.apply(oldValue, newValue);
                } else {
                    description =
                            generateDefaultChangeDescription(field.getName(), oldValue, newValue);
                }

                changes.add(new FieldChange(field.getName(), oldValue, newValue, description));
            }
        }

        return changes;
    }

    /**
     * Generate a default description for a field change when no custom handler is available.
     *
     * @param fieldName The name of the field.
     * @param oldValue The old value of the field.
     * @param newValue The new value of the field.
     * @return A default string description of the field change.
     */
    private static String generateDefaultChangeDescription(
            String fieldName, Object oldValue, Object newValue) {
        return fieldName
                + " changed from '"
                + (oldValue != null ? oldValue : "N/A")
                + "' to '"
                + (newValue != null ? newValue : "N/A")
                + "'";
    }

    /** Represents a single field change in the audit log. */
    public static class FieldChange {
        private final String fieldName;
        private final Object oldValue;
        private final Object newValue;
        private final String description;

        public FieldChange(String fieldName, Object oldValue, Object newValue, String description) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.description = description;
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

        public String getDescription() {
            return description;
        }
    }
}
