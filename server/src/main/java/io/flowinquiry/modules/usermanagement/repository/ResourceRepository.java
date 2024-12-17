package io.flowinquiry.modules.usermanagement.repository;

import io.flowinquiry.modules.usermanagement.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, String> {}
