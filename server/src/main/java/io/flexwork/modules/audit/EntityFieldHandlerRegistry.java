package io.flexwork.modules.audit;

import io.flexwork.modules.collab.domain.EntityType;
import java.util.function.BiFunction;

public interface EntityFieldHandlerRegistry {

    /**
     * Get a handler for a specific field.
     *
     * @param fieldName The name of the field to handle.
     * @return A handler function that processes the old and new values of the field.
     */
    BiFunction<Object, Object, String> getHandler(String fieldName);

    /**
     * Get the class of the entity this registry handles.
     *
     * @return The entity's class.
     */
    Class<?> getEntityClass();

    /**
     * Get the EntityType associated with this registry.
     *
     * @return The EntityType of the entity.
     */
    EntityType getEntityType();
}
