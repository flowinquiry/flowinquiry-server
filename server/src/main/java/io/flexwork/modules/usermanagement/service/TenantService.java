package io.flexwork.modules.usermanagement.service;

import static io.flexwork.db.DbConstants.DEFAULT_TENANT;

import io.flexwork.db.service.LiquibaseService;
import io.flexwork.modules.usermanagement.domain.Tenant;
import io.flexwork.modules.usermanagement.repository.TenantRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
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
    @Transactional
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
        liquibaseService.createTenantDbSchema(tenant.getNameId());
        return tenant;
    }

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
