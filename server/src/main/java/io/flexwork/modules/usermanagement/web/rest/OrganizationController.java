package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.usermanagement.domain.Organization;
import io.flexwork.modules.usermanagement.service.OrganizationService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
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

    // Find all organizations
    @GetMapping
    public ResponseEntity<List<Organization>> findAllOrganizations() {
        List<Organization> organizations = organizationService.findAllOrganizations();
        return ResponseEntity.ok(organizations);
    }
}
