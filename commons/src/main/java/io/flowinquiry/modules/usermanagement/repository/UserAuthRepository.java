package io.flowinquiry.modules.usermanagement.repository;

import io.flowinquiry.modules.usermanagement.domain.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {}
