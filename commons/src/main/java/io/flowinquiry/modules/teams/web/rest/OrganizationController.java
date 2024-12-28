package io.flowinquiry.modules.teams.web.rest;

import io.flowinquiry.modules.teams.domain.Organization;
import io.flowinquiry.modules.teams.service.OrganizationService;
import io.flowinquiry.modules.teams.service.dto.OrganizationDTO;
import io.flowinquiry.modules.teams.service.mapper.OrganizationMapper;
import io.flowinquiry.query.QueryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private OrganizationService organizationService;

    private OrganizationMapper organizationMapper;

    public OrganizationController(
            OrganizationMapper organizationMapper, OrganizationService organizationService) {
        this.organizationMapper = organizationMapper;
        this.organizationService = organizationService;
    }

    // Create a new organization
    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody Organization organization) {
        Organization createdOrganization = organizationService.createOrganization(organization);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganization);
    }

    // Update an existing organization
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(
            @PathVariable Long id, @RequestBody Organization organization) {
        Organization updatedOrganization = organizationService.updateOrganization(id, organization);
        return ResponseEntity.ok(updatedOrganization);
    }

    // Delete an organization by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    // Find an organization by ID
    @GetMapping("/{id}")
    public ResponseEntity<Organization> findOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.findOrganizationById(id);
        return ResponseEntity.ok(organization);
    }

    // Find organizations
    @PostMapping("/search")
    public ResponseEntity<Page<OrganizationDTO>> findOrganizations(
            @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        Page<Organization> teams = organizationService.findOrganizations(queryDTO, pageable);
        return new ResponseEntity<>(teams.map(organizationMapper::toDto), HttpStatus.OK);
    }
}
