package io.flowinquiry;

import static io.flowinquiry.db.DbConstants.DEFAULT_TENANT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(
        value = {
            DefaultTenantContextBeforeEachCallback.class,
            DefaultTenantContextAfterEachCallback.class
        })
public @interface DefaultTenantContext {
    String value() default DEFAULT_TENANT;
}
