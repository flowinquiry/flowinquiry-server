package io.flowinquiry.modules.usermanagement.service;

import io.flowinquiry.config.FlowInquiryProfiles;
import io.flowinquiry.db.service.LiquibaseService;
import io.flowinquiry.modules.usermanagement.domain.Tenant;
import io.flowinquiry.modules.usermanagement.repository.TenantRepository;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static io.flowinquiry.db.DbConstants.DEFAULT_TENANT;

@Service
@Transactional
public class TenantService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantService.class);

    private TenantRepository tenantRepository;

    private LiquibaseService liquibaseService;

    public TenantService(TenantRepository tenantRepository, LiquibaseService liquibaseService) {
        this.tenantRepository = tenantRepository;
        this.liquibaseService = liquibaseService;
    }

    /**
     * @param tenant
     * @return the tenant realm
     */
    @SneakyThrows
    public Tenant registerNewTenant(Tenant tenant) {
        LOG.info("Registering new tenant: {}", tenant);

        Optional<Tenant> existingTenant = tenantRepository.findByNameIgnoreCase(tenant.getName());
        if (existingTenant.isPresent()) {
            throw new IllegalArgumentException("Tenant already exists: " + tenant.getName());
        }

        existingTenant = tenantRepository.findByDomainContainingIgnoreCase(tenant.getDomain());
        if (existingTenant.isPresent()) {
            throw new IllegalArgumentException("Domain already exists: " + tenant.getDomain());
        }

        LOG.debug("Registering new tenant: {}", tenant.getName());

        // database does not accept the first character is numeric, so create a random alphabet
        // character
        String uuid =
                (char) (new Random().nextInt(24) + 'a')
                        + UUID.randomUUID().toString().replace("-", "");

        tenant.setNameId(uuid);

        tenantRepository.save(tenant);
        liquibaseService.createTenantDbSchema(
                tenant.getNameId(), List.of(FlowInquiryProfiles.SPRING_PROFILE_PRODUCTION));
        return tenant;
    }

    @Transactional(readOnly = true)
    public Tenant getDefaultTenant() {
        return tenantRepository
                .findByNameIgnoreCase(DEFAULT_TENANT)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Default tenant " + DEFAULT_TENANT + " not found"));
    }

    public Page<Tenant> findAllTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable);
    }
}
