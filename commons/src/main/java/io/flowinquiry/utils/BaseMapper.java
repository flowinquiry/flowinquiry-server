package io.flowinquiry.utils;

public interface BaseMapper {

    default <T> T toStub(Long id, Class<T> type) {
        if (id == null) return null;
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            var idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(instance, id);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Could not create stub for " + type.getSimpleName(), e);
        }
    }
}
