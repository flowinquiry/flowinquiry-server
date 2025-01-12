package io.flowinquiry.modules.usermanagement.controller;

import io.flowinquiry.modules.usermanagement.repository.ResourceRepository;
import io.flowinquiry.modules.usermanagement.service.dto.ResourceDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAllResources() {
        List<ResourceDTO> resources =
                resourceRepository.findAll().stream()
                        .map(
                                resource ->
                                        new ResourceDTO(
                                                resource.getName(), resource.getDescription()))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}
