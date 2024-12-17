package io.flowinquiry.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.flowinquiry.IntegrationTest;
import io.flowinquiry.modules.usermanagement.domain.Tenant;
import io.flowinquiry.modules.usermanagement.service.TenantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TenantServiceIT {

    @Autowired private TenantService tenantService;

    @Test
    public void testRegisterNewTenantSuccessfully() {
        Tenant tenant = Tenant.builder().name("tenant_name").domain("tenant_domain").build();

        Tenant savedTenant = tenantService.registerNewTenant(tenant);
        assertAll(
                () -> assertEquals(tenant.getName(), savedTenant.getName()),
                () -> assertEquals(tenant.getDomain(), savedTenant.getDomain()),
                () -> assertNotNull(savedTenant.getNameId()));
    }
}
