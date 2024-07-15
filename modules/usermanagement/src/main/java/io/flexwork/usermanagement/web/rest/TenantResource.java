package io.flexwork.usermanagement.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TenantResource {

    private static final Logger log = LoggerFactory.getLogger(TenantResource.class);

    @PostMapping
    public void createTenant() {

    }
}