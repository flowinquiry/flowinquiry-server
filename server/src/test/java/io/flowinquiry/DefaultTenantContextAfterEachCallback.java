package io.flowinquiry;

import io.flowinquiry.db.TenantContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DefaultTenantContextAfterEachCallback implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        DefaultTenantContext defaultTenantContextAnnotation =
                context.getRequiredTestClass().getAnnotation(DefaultTenantContext.class);
        if (defaultTenantContextAnnotation != null) {
            TenantContext.setCurrentTenant(defaultTenantContextAnnotation.value());
        }
    }
}
