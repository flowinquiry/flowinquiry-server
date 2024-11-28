package io.flexwork.modules.audit;

import io.flexwork.modules.collab.domain.EntityType;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEntityFieldHandlerRegistry implements EntityFieldHandlerRegistry {

    private final Map<String, EntityFieldHandler> fieldHandlers = new HashMap<>();

    /**
     * Add a field handler for a specific field.
     *
     * @param fieldName The name of the field.
     * @param handler A function that processes the old and new values of the field.
     */
    protected void addFieldHandler(String fieldName, EntityFieldHandler handler) {
        fieldHandlers.put(fieldName, handler);
    }

    /**
     * Get the handler for a specific field.
     *
     * @param fieldName The name of the field.
     * @return A handler function that processes the old and new values of the field.
     */
    @Override
    public EntityFieldHandler getHandler(String fieldName) {
        return fieldHandlers.get(fieldName);
    }

    /**
     * Initialize field handlers for this entity. Subclasses must define their field-specific logic
     * in this method.
     */
    protected abstract void initializeFieldHandlers();

    /** Constructor to ensure field handlers are initialized in subclasses. */
    public AbstractEntityFieldHandlerRegistry() {
        initializeFieldHandlers();
    }

    /**
     * Subclasses must provide the entity's class.
     *
     * @return The class of the entity this registry handles.
     */
    @Override
    public abstract Class<?> getEntityClass();

    /**
     * Subclasses must provide the entity's EntityType.
     *
     * @return The EntityType of the entity.
     */
    @Override
    public abstract EntityType getEntityType();
}
