package io.flowinquiry.modules.audit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityFieldHandlerRegistryFactory {

    private final Map<Class<?>, EntityFieldHandlerRegistry> registryMap = new HashMap<>();

    @Autowired
    public EntityFieldHandlerRegistryFactory(List<EntityFieldHandlerRegistry> registries) {
        for (EntityFieldHandlerRegistry registry : registries) {
            Class<?> entityClass = registry.getEntityClass();
            registryMap.put(entityClass, registry);
        }
    }

    public EntityFieldHandlerRegistry getRegistry(Class<?> entityClass) {
        return registryMap.get(entityClass);
    }
}
